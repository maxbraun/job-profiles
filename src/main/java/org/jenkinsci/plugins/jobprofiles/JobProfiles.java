package org.jenkinsci.plugins.jobprofiles;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.BuildableItem;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;
import net.sf.json.JSONObject;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.jenkinsci.plugins.jobprofiles.JobSetupConfig.get;


public class JobProfiles extends Builder {


    @DataBoundConstructor
    public JobProfiles() {
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException {
        PrintStream log = listener.getLogger();
        SoftwareIndex index;
        Map<String, Object> context;
        Writer writer;
        Template template;
        Reader reader;
        Configuration conf;
        InputStream src;
        BuildableItem p;
        Map<String, String> profile;

        conf = new Configuration();
        log.println("Going to parse" + get().getSoftwareIndexFile());
        index = Parser.parse(get().getSoftwareIndexFile());
        log.println("Parsed.");
        log.println(index.toString());

        for (SoftwareAsset asset : index.getAssets()) {
            Scm scm = new ScmFactory(asset.getScm()).get();
            context = new ContextFactory(scm).get().getContext();
            log.println("Creating Job for " + asset.getName());
            writer = new StringWriter();

            context.put("name", asset.getName());
            context.put("scm", asset.getScm());

            //TODO: need a profilefinder
            profile = new ScmFactory(get().getProfileRootDir()).get().getProfile("git-maven");
            try {
                for (Map.Entry<String, String> entry : profile.entrySet()) {

                    reader = new StringReader(entry.getValue());
                    template = new Template("", reader, conf);
                    template.process(context, writer);
                    src = new ByteArrayInputStream(writer.toString().getBytes());
                    p = (BuildableItem) Jenkins.getInstance().createProjectFromXML(asset.getName().toLowerCase() + " " + entry.getKey(), src);
                    src.close();

                }
            } catch (TemplateException e) {
                throw new JobProfileException(e.getMessage(), e.getCause());
            }

        }
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link JobProfiles}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/hello_world/JobProfiles/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Job Updates";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            //useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }
    }
}


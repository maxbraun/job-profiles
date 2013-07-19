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
import net.oneandone.sushi.fs.CreateInputStreamException;
import net.oneandone.sushi.fs.FileNotFoundException;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;
import net.sf.json.JSONObject;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.jenkinsci.plugins.jobprofiles.JobSetupConfig.*;


public class JobProfiles extends Builder {


    @DataBoundConstructor
    public JobProfiles() {
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException {
        PrintStream log = listener.getLogger();
        SoftwareIndex index;
        String pom;
        MavenProject mp;
        Map<String, String> context;
        Writer writer;
        Template template;
        Reader reader;
        Configuration conf;
        InputStream src;
        BuildableItem p;

        conf = new Configuration();
        log.println("Going to parse" + get().getSoftwareIndexFile());
        index = Parser.parse(get().getSoftwareIndexFile());
        log.println("Parsed.");
        log.println(index.toString());

        for (SoftwareAsset asset : index.getAssets()) {
            context = new HashMap<String, String>();
            log.println("Creating Job for " + asset.getName());
            pom = new ScmGit().getPom(asset.getScm());
            writer = new StringWriter();

            assert !pom.isEmpty();
            mp = MavenProcessor.MavenProcessor(pom, new World(), listener, build);
            context.put("name", mp.getName());
            context.put("scm", asset.getScm());

            ProfileGit profiles = new ProfileGit(get().getProfileRootDir(), "git-maven");
            try {
                for (Map.Entry<String, String> entry : profiles.getProfiles().entrySet()) {

                    reader = new StringReader(entry.getValue());
                    template = new Template("", reader, conf);
                    template.process(context, writer);
                    src = new ByteArrayInputStream(writer.toString().getBytes());
                    p = (BuildableItem) Jenkins.getInstance().createProjectFromXML(mp.getName().toLowerCase() + " " + entry.getKey(), src);
                    src.close();

                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
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


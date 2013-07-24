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
import hudson.model.ListView;
import hudson.model.View;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
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
        ProfileFinder profileFinder;
        String name;
        World world;

        world = new World();
        conf = new Configuration();

        log.println("Going to parse" + get().getSoftwareIndexFile());

        index = Parser.parse(get().getSoftwareIndexFile(), world);

        log.println("Parsed.");

        profileFinder = new ProfileFinder(get().getProfileRootDir(), world);

        for (SoftwareAsset asset : index.getAssets()) {

            Scm scm = Scm.get(asset.getScm(), world);
            context = Context.get(scm, world).getContext();
            log.println("Creating Job for " + asset.getName());
            writer = new StringWriter();

            context.put("name", asset.getName());
            context.put("scm", asset.getScm());

            //TODO: need a extended profilefinder
            profile = profileFinder.setAssetSCM(scm).getProfile();
            try {
                for (Map.Entry<String, String> entry : profile.entrySet()) {
                    name = asset.getName().toLowerCase() + "_" + entry.getKey().replace(".xml", "");
                    reader = new StringReader(entry.getValue());
                    template = new Template("", reader, conf);
                    template.process(context, writer);
                    src = new ByteArrayInputStream(writer.toString().getBytes());
                    p = (BuildableItem) Jenkins.getInstance()
                            .createProjectFromXML(name, src);
                    src.close();


                    if (Jenkins.getInstance().getView(asset.getCategory()) == null) {
                        View view = new ListView(asset.getCategory());
                        Jenkins.getInstance().addView(view);
                    }

                    ListView view = (ListView) Jenkins.getInstance().getView(asset.getCategory());
                    view.doAddJobToView(name);

                }
            } catch (TemplateException e) {
                throw new JobProfileException(e.getMessage(), e.getCause());
            } catch (ServletException e) {
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


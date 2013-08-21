package org.jenkinsci.plugins.jobprofiles;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.tmatesoft.svn.core.SVNAuthenticationException;

import javax.servlet.ServletException;
import java.io.*;
import java.util.Date;
import java.util.Map;

import static org.jenkinsci.plugins.jobprofiles.JobProfilesConfiguration.get;


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
        ProfileManager profileManager;
        String name, key;
        World world;

        world = new World();
        conf = new Configuration();

        log.println("Going to parse " + get().getSoftwareIndexFile());

        index = SoftwareIndex.load(world.validNode(get().getSoftwareIndexFile()));

        log.println("Parsed.");

        log.println("Downloading Profiles");
        profileManager = new ProfileManager(world, log, get().getProfileRootDir());

        for (SoftwareAsset asset : index.getAssets()) {

            Scm scm = Scm.get(asset.getTrunk(), world);
            log.println("Creating Job for " + asset.getArtifactId());

            context = Context.get(scm, world);
            context.put("name", asset.getArtifactId());
            context.put("scm", asset.getTrunk());
            context.put("version", "Last_modified: " + new Date(build.getStartTimeInMillis()) + ",Yellow_Page_ID: " + asset.getId());
            //asset.setType(profileFinder.setAssetSCM(scm).findBuildSystem());

            profile = profileManager.discover(scm, null).getProfile(log);
            try {
                for (Map.Entry<String, String> entry : profile.entrySet()) {
                    writer = new StringWriter();
                    key = "user_" + asset.getGroupId() + "_" + asset.getArtifactId().toLowerCase();
                    if (entry.getKey().equals("build.xml")) {
                        name = key + "_" + entry.getKey().replace (".xml", "");
                    } else {
                        name = key;
                    }
                    context.put("id", key);
                    reader = new StringReader(entry.getValue());
                    template = new Template(name, reader, conf);
                    template.process(context, writer);

                    if (writer.toString().length() == 0) continue;

                    src = new ByteArrayInputStream(writer.toString().getBytes());
                    p = (BuildableItem) Jenkins.getInstance()
                            .createProjectFromXML(name, src);
                    src.close();

                    removeJobFromViews(name, log);
                    
                    if (Jenkins.getInstance().getView(asset.getCategory()) == null) {
                        View view = new ListView(asset.getCategory());
                        Jenkins.getInstance().addView(view);
                    }

                    ListView view = (ListView) Jenkins.getInstance().getView(asset.getCategory());
                    view.doAddJobToView(name);

                }
            } catch (TemplateException e) {
                throw new JobProfileException(e.getMessage(), e);
            } catch (ServletException e) {
                throw new JobProfileException(e.getMessage(), e);
            }
        }
        return true;
    }

    public static void removeJobFromViews(String jobId, PrintStream log) {
        for (View view : Jenkins.getInstance().getViews()) {
            view.onJobRenamed(null, jobId, null);
        }

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


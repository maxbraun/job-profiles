package org.jenkinsci.plugins.jobprofiles;

import freemarker.template.TemplateException;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import lombok.Getter;
import lombok.Setter;
import net.oneandone.sushi.fs.World;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.*;
import java.util.*;

import static org.jenkinsci.plugins.jobprofiles.JobProfilesConfiguration.get;


@Getter
@Setter
public class JobProfiles extends Builder {

    private final String forcedSCM;
    private final String forcedProfile;

    @DataBoundConstructor
    public JobProfiles(String forcedSCM, String forcedProfile) {
        this.forcedSCM = forcedSCM;
        this.forcedProfile = forcedProfile;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException {
        PrintStream log = listener.getLogger();
        SoftwareIndex index;
        ProfileManager profileManager;
        String forcedProfile;
        World world;
        Set<Job> jobs;

        world = new World();

        forcedProfile = null;

        if (!Util.replaceMacro(forcedSCM, build.getBuildVariableResolver()).isEmpty()) {
            SoftwareAsset asset;
            asset = new SoftwareAsset();
            index = new SoftwareIndex();

            if (Util.replaceMacro(forcedSCM, build.getBuildVariableResolver()).equals("system")) {

                asset.setId("system");
                asset.setArtifactId("system");
                asset.setCategory("System");
                asset.setGroupId("system");
                asset.setTrunk("system");

            } else {

                asset.setTrunk(Util.replaceMacro(forcedSCM, build.getBuildVariableResolver()));
            }
            index.asset.add(asset);

        } else {

            log.println("Going to parse " + get().getSoftwareIndexFile());
            index = SoftwareIndex.load(world.validNode(get().getSoftwareIndexFile()));
            log.println("Parsed.");
        }

        if (!Util.replaceMacro(this.forcedProfile, build.getBuildVariableResolver()).isEmpty()) {

            forcedProfile = Util.replaceMacro(this.forcedProfile, build.getBuildVariableResolver());
            log.println("Using a forced Profile: " + forcedProfile);

        }


        log.println("Downloading Profiles");
        profileManager = new ProfileManager(world, log, get().getProfileRootDir());

        jobs = createJobs(log, index, profileManager, forcedProfile, world);

        try {
            for (Job job: jobs) {
                job.sendParsedTemplatesToInstance();
                job.manageViews();
            }
        } catch (ServletException e) {
            throw new JobProfileException(e);
        }

        return true;
    }

    public static Set<Job> createJobs(PrintStream log, SoftwareIndex index, ProfileManager profileManager, String forcedProfile, World world)
            throws IOException {
        Set<Job> jobs;
        Job currentJob;

        jobs = new HashSet<Job>();

        for (SoftwareAsset asset : index.getAssets()) {
            currentJob = Job.Job(asset, world);

            profileManager.discover(currentJob.getScm(), forcedProfile).getProfile();

            currentJob.setUsedProfile(profileManager.profile);
            try {
                Context.add(currentJob, world);
                currentJob.createParsedTemplates(log, profileManager.getProfile());
                jobs.add(currentJob);
            } catch (TemplateException e) {
                throw new JobProfileException(e.getMessage(), e);
            }
            jobs.add(currentJob);
        }
        return jobs;
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


package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import net.oneandone.sushi.fs.World;
import net.sf.json.JSONObject;


public class JobProfiles extends hudson.tasks.Builder {

    private final JobBuilder builder = new JobBuilder();
    private String forcedSCM;
    private String forcedProfile;

    @DataBoundConstructor
    public JobProfiles(String forcedSCM, String forcedProfile) {
        this.forcedSCM = forcedSCM;
        this.forcedProfile = forcedProfile;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException {
        PrintStream log = listener.getLogger();
        World world;

        world = new World();

        forcedSCM = Util.replaceMacro(forcedSCM, build.getBuildVariableResolver());
        forcedProfile = Util.replaceMacro(this.forcedProfile, build.getBuildVariableResolver());

        try {
            JobBuilder.buildJobs(forcedSCM, forcedProfile, log, world);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    public JobBuilder getBuilder() {
        return this.builder;
    }
    public String getForcedSCM() {
        return this.forcedSCM;
    }
    public void setForcedSCM(String forcedSCM) {
        this.forcedSCM = forcedSCM;
    }
    public String getForcedProfile() {
        return this.forcedProfile;
    }
    public void setForcedProfile(String forcedProfile) {
        this.forcedProfile = forcedProfile;
    }


    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<hudson.tasks.Builder> {


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return Messages.JobProfiles_displayName();
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


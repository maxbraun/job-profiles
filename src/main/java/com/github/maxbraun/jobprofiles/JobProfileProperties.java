package com.github.maxbraun.jobprofiles;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;


public class JobProfileProperties extends JobProperty<AbstractProject<?, ?>> {

    private final String id;
    private final String lastModified;
    private final String usedProfile;

    @DataBoundConstructor
    public JobProfileProperties(String id, String lastModified, String usedProfile) {
        this.id = id;
        this.lastModified = lastModified;
        this.usedProfile = usedProfile;
    }
    public String getId() {
        return this.id;
    }
    public String getLastModified() {
        return this.lastModified;
    }
    public String getUsedProfile() {
        return this.usedProfile;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.JobProfileProperties_headline();
        }
    }
}

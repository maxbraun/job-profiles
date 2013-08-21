package org.jenkinsci.plugins.jobprofiles;


import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Map;

public class JobProfileProperties  extends BuildWrapper{
    public final Map<String, String> properties;

    @DataBoundConstructor
    public JobProfileProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Job Profile Informations";
        }
    }
}

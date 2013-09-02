package org.jenkinsci.plugins.jobprofiles;


import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;


@Getter
@Setter
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

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.JobProfileProperties_headline();
        }
    }
}

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
public class JobProfileProperties  extends JobProperty<AbstractProject<?,?>> {

    private final String id;
    private final String lastModified;

    @DataBoundConstructor
    public JobProfileProperties(String id, String lastModified) {
        this.id = id;
        this.lastModified = lastModified;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return "Job Profile Informations";
        }
    }
}

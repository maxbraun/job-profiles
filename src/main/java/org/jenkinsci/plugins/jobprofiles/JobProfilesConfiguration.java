package org.jenkinsci.plugins.jobprofiles;


import java.net.URI;
import java.net.URISyntaxException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Extension
@Slf4j
@Getter
@Setter
public class JobProfilesConfiguration extends GlobalConfiguration {

    private String softwareIndexFile;
    private String profileRootDir;

    public JobProfilesConfiguration() {
        load();
    }

    @DataBoundConstructor
    public JobProfilesConfiguration(String softwareIndexFile, String profileRootDir) {
        this.softwareIndexFile = softwareIndexFile;
        this.profileRootDir = profileRootDir;
    }
    public static JobProfilesConfiguration get() {
        return GlobalConfiguration.all().get(JobProfilesConfiguration.class);
    }
    @Override
    public boolean configure(final StaplerRequest request, final JSONObject json)
      throws FormException {
        request.bindJSON(this, json);
        save();
        return true;
    }
    @Override
    public String getDisplayName() {
        return Messages.JobProfiles_displayName();
    }

    public FormValidation doCheckValidUri(@QueryParameter String value) {
        try {
            new URI(value);
        } catch (URISyntaxException e) {
            return FormValidation.error(Messages.JobProfilesConfiguration_failed());
        }
        return FormValidation.ok();
    }
}

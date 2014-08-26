package com.github.maxbraun.jobprofiles;

import java.net.URI;
import java.net.URISyntaxException;

import org.jenkinsci.plugins.jobprofiles.Messages;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

@Extension
public class JobProfilesConfiguration extends GlobalConfiguration {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JobProfilesConfiguration.class);
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
    public String getSoftwareIndexFile() {
        return this.softwareIndexFile;
    }
    public void setSoftwareIndexFile(String softwareIndexFile) {
        this.softwareIndexFile = softwareIndexFile;
    }
    public String getProfileRootDir() {
        return this.profileRootDir;
    }
    public void setProfileRootDir(String profileRootDir) {
        this.profileRootDir = profileRootDir;
    }
}

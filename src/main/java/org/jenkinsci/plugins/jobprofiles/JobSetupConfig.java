package org.jenkinsci.plugins.jobprofiles;


import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.net.URI;
import java.net.URISyntaxException;

@Extension
@Slf4j
@Getter
@Setter
public class JobSetupConfig extends GlobalConfiguration {

    private static final String SOFTWARE_INDEX_FILE_DEFAULT = "svn:https://github.com/maxbraun/job-profiles/trunk/src/main/resources/softreg.xml";
    private static final String PROFILE_ROOT_DIR_DEFAULT = "https://github.com/maxbraun/job-profiles-examles.git";

    private String softwareIndexFile;
    private String profileRootDir;

    public JobSetupConfig() {
        softwareIndexFile = SOFTWARE_INDEX_FILE_DEFAULT;
        profileRootDir = PROFILE_ROOT_DIR_DEFAULT;
    }

    @DataBoundConstructor
    public JobSetupConfig(String softwareIndexFile, String profileRootDir) {
        this.softwareIndexFile = softwareIndexFile;
        this.profileRootDir = profileRootDir;
    }

    @Override
    public boolean configure(final StaplerRequest request, final JSONObject json)
            throws FormException {
        request.bindJSON(this, json);
        save();
        return true;
    }

    public static JobSetupConfig get() {
        return GlobalConfiguration.all().get(JobSetupConfig.class);
    }

    @Override
    public String getDisplayName() {
        return "Job Profiles";
    }

    public FormValidation doCheckValidUri(@QueryParameter String value) {
        try {
            new URI(value);
        } catch (URISyntaxException e) {
            return FormValidation.error("Validation failed.");
        }
        return FormValidation.ok();
    }
}

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

@Extension
@Slf4j
@Getter
@Setter
public class JobSetupConfig extends GlobalConfiguration {

    private static final String softwareIndexFileDefault = "svn:https://github.com/maxbraun/job-profiles/trunk/src/main/resources/softreg.xml";

    private String softwareIndexFile;

    public JobSetupConfig() {
        softwareIndexFile = softwareIndexFileDefault;
    }

    @DataBoundConstructor
    public JobSetupConfig(String softwareIndexFile) {
        this.softwareIndexFile = softwareIndexFile;
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

    public FormValidation doCheckSoftwareIndexFile(@QueryParameter String value) {
        World world;

        world = new World();
        try {
            world.validNode(value);
            return FormValidation.ok();
        } catch (NodeInstantiationException e) {
            log.info("could not validate {}, cause {}", value, e);
            return FormValidation.error("Validation failed");
        } catch (IllegalStateException e) {
            log.info("could not validate {}, cause {}", value, e);
            return FormValidation.error("Validation failed. Try it with file://");
        }
    }


}

package org.jenkinsci.plugins.jobprofiles;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.oneandone.sushi.fs.CreateInputStreamException;
import net.oneandone.sushi.fs.FileNotFoundException;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;
import net.sf.json.JSONObject;
import org.apache.maven.project.MavenProject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.PrintStream;


public class JobProfiles extends Builder {


    @DataBoundConstructor
    public JobProfiles() {
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws FileNotFoundException, NodeInstantiationException, CreateInputStreamException {
        PrintStream log = listener.getLogger();
        SoftwareIndex index;
        String pom;
        MavenProject mp;

        log.println("Going to parse" + JobSetupConfig.get().getSoftwareIndexFile());
        index = Parser.parse(JobSetupConfig.get().getSoftwareIndexFile());
        log.println("Parsed.");
        log.println(index.toString());

        for (SoftwareAsset asset : index.getAssets()) {
            log.println("Creating Job for " + asset.getName());
            pom = new ScmGit().getPom(asset.getScm());
            if (!pom.isEmpty()) {
                mp = MavenProcessor.MavenProcessor(pom, new World(), listener, build);
            }


        }
        return true;
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


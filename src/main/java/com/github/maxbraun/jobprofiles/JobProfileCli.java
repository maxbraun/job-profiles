package com.github.maxbraun.jobprofiles;

import org.kohsuke.args4j.Argument;

import hudson.Extension;
import hudson.cli.CLICommand;
import net.oneandone.sushi.fs.svn.SvnNode;
import net.oneandone.sushi.util.Strings;

@Extension
public class JobProfileCli extends CLICommand {

    @Argument(index = 0, metaVar = "SCM", required = false)
    public String forcedSCM;

    @Argument(index = 1, metaVar = "Forced Profile", required = false)
    public String forcedProfile;

    @Override
    public String getShortDescription() {
        return Messages.JobProfileCli_shortDescription();
    }

    protected
    int run() throws Exception {
        try {

            SoftwareIndex softwareIndex = SoftwareIndex.buildFrom(forcedSCM, stdout);
            SvnNode profileRoot = new SvnNodeBuilder(Strings.removeRightOpt(JobProfilesConfiguration.get().getProfileRootDir(), "/")).build();
            Profiles profiles = Profiles.fromNode(profileRoot);
            JobsCreator jobsCreator = new JobsCreator(profiles, new MavenProjectResolver(profileRoot.getWorld()), forcedProfile, stdout);

            for (SoftwareAsset softwareAsset : softwareIndex) {
                Jobs jobs = jobsCreator.createJobs(softwareAsset);

                jobs.submit(stdout);
            }

        } catch (Exception e) {
            stderr.println(e.getMessage());
            e.printStackTrace(stderr);
            return 1;
        }
        return 0;
    }


}
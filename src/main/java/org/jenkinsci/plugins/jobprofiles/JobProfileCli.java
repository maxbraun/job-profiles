package org.jenkinsci.plugins.jobprofiles;


import hudson.Extension;
import hudson.cli.CLICommand;
import net.oneandone.sushi.fs.World;
import org.kohsuke.args4j.Argument;

@Extension
public class JobProfileCli extends CLICommand {

    @Argument(index = 0, metaVar = "SCM", required = false)
    public String forcedSCM;

    @Argument(index = 1, metaVar = "Forced Profile", required = false)
    public String forcedProfile;

    @Override
    public String getShortDescription() {
        return "Updates Jenkins Jobs with the Job Profiles Plugin";
    }

    protected int run() throws Exception {
        Jobs.buildJobs(forcedSCM, forcedProfile, stdout, new World());
        return 0;
    }


}
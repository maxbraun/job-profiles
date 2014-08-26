package org.jenkinsci.plugins.jobprofiles;


import org.kohsuke.args4j.Argument;

import hudson.Extension;
import hudson.cli.CLICommand;
import net.oneandone.sushi.fs.World;

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

    protected int run() throws Exception {
        JobBuilder.buildJobs(forcedSCM, forcedProfile, stdout, new World());
        return 0;
    }


}
package org.jenkinsci.plugins.jobprofiles;


import hudson.cli.declarative.CLIMethod;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.Argument;

@Slf4j
public class JobProfileCLI {
    @CLIMethod(name = "updateJobs")
    public static void updateJobs(@Argument(index = 0) String forcedSCM, @Argument(index = 1) String forcedProfile) {
        log.info(forcedProfile);
        log.info(forcedSCM);
    }


}

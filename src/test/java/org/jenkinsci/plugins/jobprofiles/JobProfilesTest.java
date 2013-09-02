package org.jenkinsci.plugins.jobprofiles;

import hudson.Launcher;
import hudson.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;

@Slf4j
public class JobProfilesTest {
    @Rule public JenkinsRule j = new JenkinsRule();
    @Test public void systemJobs() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new JobProfiles("system", "system"));
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        build.writeWholeLogTo(System.out);

        Assert.assertEquals(Result.SUCCESS, build.getResult());

    }

    @Test public void NormalJobs() throws Exception {
        j.configureMaven3();
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new JobProfiles("", ""));
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        build.writeWholeLogTo(System.out);

        Assert.assertEquals(Result.SUCCESS, build.getResult());

    }


}
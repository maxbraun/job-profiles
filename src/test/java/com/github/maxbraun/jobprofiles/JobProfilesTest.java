package com.github.maxbraun.jobprofiles;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.FreeStyleProject;


public class JobProfilesTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void setup() throws Exception {
        JobProfilesConfiguration.get().setSoftwareIndexFile("https://raw.githubusercontent.com/maxbraun/job-profiles/master/src/test/resources/index.json");
        JobProfilesConfiguration.get().setProfileRootDir("https://github.com/maxbraun/job-profiles-examles/trunk");
        j.configureMaven3();
    }
    @Test
    public void systemJobs() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();

        project.getBuildersList().add(new JobProfiles("system", "system"));
        j.buildAndAssertSuccess(project);

        j.buildAndAssertSuccess(project);

    }

    @Test
    public void NormalJobs() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new JobProfiles("", ""));
        j.buildAndAssertSuccess(project);
    }


}
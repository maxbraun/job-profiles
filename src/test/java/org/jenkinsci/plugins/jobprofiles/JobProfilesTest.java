package org.jenkinsci.plugins.jobprofiles;

import hudson.model.FreeStyleProject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


@Slf4j
public class JobProfilesTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void setup() throws Exception {
        JobProfilesConfiguration.get().setSoftwareIndexFile("svn:https://github.com/maxbraun/job-profiles/trunk/src/main/resources/softreg.xml");
        JobProfilesConfiguration.get().setProfileRootDir("https://github.com/maxbraun/job-profiles-examles.git");
        j.configureMaven3();
    }
    @Test
    public void systemJobs() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();

        project.getBuildersList().add(new JobProfiles("system", "system"));
        j.buildAndAssertSuccess(project);



    }

    @Test
    public void NormalJobs() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new JobProfiles("", ""));
        j.buildAndAssertSuccess(project);
    }


}
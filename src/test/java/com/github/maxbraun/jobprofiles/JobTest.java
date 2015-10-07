package com.github.maxbraun.jobprofiles;

import org.junit.Test;

import net.oneandone.sushi.fs.World;


public class JobTest {
    @Test
    public void testAddContext() throws Exception {

    }

    @Test
    public void testJob() throws Exception {
        ProfileManager manager = new ProfileManager(new World(), System.out, "https://github.com/maxbraun/job-profiles-examles/trunk");
        SoftwareAsset foo = new SoftwareAsset("https://github.com/mlhartme/sushi/", new Coordinates("sushi", "net.oneandone.devel"));

        Job job = Job.create(foo, new World(), System.out);
        Profile profile = manager.getProfileForScm(job.scm(), null);
        job.setProfile(profile);
        job.parseProfile(System.out);

    }

    @Test
    public void testGetparsedTemplates() throws Exception {

    }
}

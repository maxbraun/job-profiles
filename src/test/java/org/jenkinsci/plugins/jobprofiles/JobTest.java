package org.jenkinsci.plugins.jobprofiles;

import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.World;
import org.junit.Test;

@Slf4j
public class JobTest {
    @Test
    public void testAddContext() throws Exception {

    }

    @Test
    public void testJob() throws Exception {
        ProfileManager manager = new ProfileManager(new World(), System.out, "https://github.com/maxbraun/job-profiles-examles/trunk");
        SoftwareAsset foo = new SoftwareAsset();
        foo.setId("1");
        foo.setArtifactId("sushi");
        foo.setGroupId("net.oneandone.devel");
        foo.setTrunk("https://github.com/mlhartme/sushi.git");
        foo.setCategory("Library");

        Job job = Job.create(foo, new World());
        Profile profile = manager.getProfileForScm(job.getScm(), null);
        job.setProfile(profile);
        job.parseProfile(System.out);
        log.info(job.toString());

    }

    @Test
    public void testGetparsedTemplates() throws Exception {

    }
}

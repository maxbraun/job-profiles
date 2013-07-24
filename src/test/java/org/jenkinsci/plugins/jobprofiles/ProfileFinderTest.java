package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.World;
import org.junit.Assert;
import org.junit.Test;

public class ProfileFinderTest {
    private final String profiles = "https://github.com/maxbraun/job-profiles-examles";
    private final String maven = "https://github.com/mlhartme/sushi.git";
    private final String composer = "https://github.com/holger/yoshi.git";
    private final World world = new World();
    private ProfileFinder finder;
    //pear packager
    //yeoman
    //grunt

    @Test
    public void maven() throws Exception {
        finder = new ProfileFinder(profiles, world);
        Assert.assertEquals("maven", finder.setAssetSCM(Scm.get(maven, world)).find());


    }

    @Test
    public void composer() throws Exception {
        finder = new ProfileFinder(profiles, world);
        Assert.assertEquals("composer", finder.setAssetSCM(Scm.get(composer, world)).find());

    }
}

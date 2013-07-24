package org.jenkinsci.plugins.jobprofiles;


import org.junit.Assert;
import org.junit.Test;

public class ProfileFinderTest {
    private final String profiles = "https://github.com/maxbraun/job-profiles-examles";
    private final String maven = "https://github.com/mlhartme/sushi.git";
    private final String composer = "https://github.com/holger/yoshi.git";
    private ProfileFinder finder;
    //pear packager
    //yeoman
    //grunt

    @Test
    public void maven() throws Exception {
        finder = new ProfileFinder(profiles);
        Assert.assertEquals("maven", finder.setAssetSCM(new ScmFactory(maven).get()).find());


    }

    @Test
    public void composer() throws Exception {
        finder = new ProfileFinder(profiles);
        Assert.assertEquals("composer", finder.setAssetSCM(new ScmFactory(composer).get()).find());

    }
}

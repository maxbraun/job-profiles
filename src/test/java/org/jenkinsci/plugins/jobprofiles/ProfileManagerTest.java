package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.World;
import org.junit.Assert;
import org.junit.Test;

import java.io.PrintStream;

public class ProfileManagerTest {
    private final String profiles = "https://github.com/maxbraun/job-profiles-examles";
    private final String maven = "https://github.com/mlhartme/sushi.git";
    private final String composer = "https://github.com/holger/yoshi.git";
    private final World world = new World();
    private ProfileManager manager;
    //pear packager
    //yeoman
    //grunt

    @Test
    public void maven() throws Exception {
        manager = new ProfileManager(world, new PrintStream(System.out), profiles);
        Assert.assertEquals("maven", manager.discover(Scm.get(maven, world), null).find());


    }

    @Test
    public void composer() throws Exception {
        manager = new ProfileManager(world, new PrintStream(System.out), profiles);
        Assert.assertEquals("composer", manager.discover(Scm.get(composer, world), null).find());
    }

    @Test
    public void standard() throws Exception {
        manager = new ProfileManager(world, new PrintStream(System.out), profiles);
        Assert.assertEquals("standard", manager.discover(Scm.get(profiles, world), null).find());
    }

    @Test
    public void forcedProfile() throws Exception {
        manager = new ProfileManager(world, new PrintStream(System.out), profiles);
        Assert.assertEquals("forced", manager.discover(Scm.get(profiles, world), "forced").find());
    }
}

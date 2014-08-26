package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.oneandone.sushi.fs.World;

public class ScmTest {

    private final String svn = "https://svn.code.sf.net/p/pustefix/code/trunk";
    private final World world = new World();


    @Test
    public void getPomfromSCMS() throws Exception {
        getPom(svn);
    }

    private void getPom(String scmLocation) throws IOException {
        Scm scm;
        scm = Scm.create(scmLocation, world);

        Assert.assertTrue(scm.getPom().length() > 1);
    }

    @Test(expected = JobProfileException.class)
    public void WrongSVNUrl() throws Exception {
        Scm.create(svn + "/", world).getPom();
    }


    @Test
    public void SvnProfile() throws Exception {
        getProfile("https://github.com/maxbraun/job-profiles-examles/trunk", "maven");
    }

    private void getProfile(String scmUrl, String profileName) throws Exception {
        Scm scm;
        scm = Scm.create(scmUrl, world);
        Map<String, String> profile;

        profile = scm.getProfile(profileName, new PrintStream(System.out));

        Assert.assertTrue(profile.size() >= 1);
    }
}

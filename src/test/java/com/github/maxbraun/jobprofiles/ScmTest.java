package com.github.maxbraun.jobprofiles;

import java.io.IOException;

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
        ScmNode scm;

        scm = new ScmNode(new SvnNodeBuilder(scmLocation).build());

        Assert.assertTrue(scm.getPom().length() > 1);
    }


    @Test
    public void SvnProfile() throws Exception {
        getProfile("https://github.com/maxbraun/job-profiles-examles/trunk", "maven");
    }

    private void getProfile(String scmUrl, String profileName) throws Exception {

        Profiles profiles = Profiles.fromNode(new SvnNodeBuilder(scmUrl).build());

        Assert.assertTrue(profiles.size() > 0);
    }
}

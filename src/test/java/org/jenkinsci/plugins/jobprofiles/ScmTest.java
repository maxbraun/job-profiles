package org.jenkinsci.plugins.jobprofiles;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScmTest {

    private String svn;
    private String git;

    @Before
    public void init() throws Exception {
        svn = "https://pustefix.svn.sourceforge.net/svnroot/pustefix/trunk";
        git = "https://github.com/mlhartme/sushi.git";
    }

    @Test
    public void getGitFromScmFactory() throws Exception {
        Scm scm;
        List<String> gitURLs;

        gitURLs = new ArrayList<String>();
        gitURLs.add("ssh://user@host.xz:port/path/to/repo.git/");
        gitURLs.add("ssh://user@host.xz/path/to/repo.git/");
        gitURLs.add("ssh://host.xz:port/path/to/repo.git/");
        gitURLs.add("ssh://host.xz/path/to/repo.git/");
        gitURLs.add("ssh://user@host.xz/path/to/repo.git/");
        gitURLs.add("ssh://host.xz/path/to/repo.git/");
        gitURLs.add("ssh://user@host.xz/~user/path/to/repo.git/");
        gitURLs.add("ssh://host.xz/~user/path/to/repo.git/");
        gitURLs.add("ssh://user@host.xz/~/path/to/repo.git");
        gitURLs.add("ssh://host.xz/~/path/to/repo.git");
        gitURLs.add("user@host.xz:/path/to/repo.git/");
        gitURLs.add("host.xz:/path/to/repo.git/");
        gitURLs.add("user@host.xz:~user/path/to/repo.git/");
        gitURLs.add("host.xz:~user/path/to/repo.git/");
        gitURLs.add("user@host.xz:path/to/repo.git");
        gitURLs.add("host.xz:path/to/repo.git");
        gitURLs.add("rsync://host.xz/path/to/repo.git/");
        gitURLs.add("git://host.xz/path/to/repo.git/");
        gitURLs.add("git://host.xz/~user/path/to/repo.git/");
        gitURLs.add("http://host.xz/path/to/repo.git/");
        gitURLs.add("https://host.xz/path/to/repo.git/");
        gitURLs.add("/path/to/repo.git/");
        gitURLs.add("path/to/repo.git/");
        gitURLs.add("~/path/to/repo.git");
        gitURLs.add("file:///path/to/repo.git/");
        gitURLs.add("file://~/path/to/repo.git/");

        for (String gitUrl : gitURLs) {
            scm = new ScmFactory(gitUrl).get();
            Assert.assertTrue(scm instanceof ScmGit);
        }
    }

    @Test
    public void getSVNFromScmFactory() throws Exception {
        Scm scm;
        scm = new ScmFactory(svn).get();
        Assert.assertTrue(scm instanceof ScmSVN);
    }

    @Test
    public void getPomfromSCMS() throws Exception {
        getPom(svn);
        getPom(git);
    }

    private void getPom(String scmLocation) {
        Scm scm;
        scm = new ScmFactory(scmLocation).get();

        Assert.assertTrue(scm.getPom().length() > 1);
    }

    @Test(expected = JobProfileException.class)
    public void WrongSVNUrl() throws Exception {
        new ScmFactory(svn + "/").get().getPom();
    }

    @Test(expected = JobProfileException.class)
    public void WrongGitUrl() throws Exception {
        new ScmFactory("http:ajdlfjlsdjfö.git").get().getPom();
    }

    @Test(expected = JobProfileException.class)
    public void GitRepoWithoutPom() throws Exception {
        new ScmFactory("git@github.com:maxbraun/puppet-phantomjs.git").get().getPom();
    }

    @Test
    public void GitProfile() throws Exception {
        getProfile("https://github.com/maxbraun/job-profiles-examles.git", "git-maven");
    }

    @Test
    public void SvnProfile() throws Exception {
        getProfile("https://github.com/maxbraun/job-profiles-examles/trunk", "git-maven");
    }

    private void getProfile(String scmUrl, String profileName) throws Exception {
        Scm scm;
        scm = new ScmFactory(scmUrl).get();
        Map<String, String> profile;

        profile = scm.getProfile(profileName);

        Assert.assertTrue(profile.size() >= 1);
    }
}

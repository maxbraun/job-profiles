package org.jenkinsci.plugins.jobprofiles;

import org.junit.Before;
import org.junit.Test;
import sun.jvm.hotspot.utilities.Assert;

import java.util.ArrayList;
import java.util.List;
public class ScmTest {

    private String svn;
    private String git;

    @Before
    public void init() throws Exception{
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
            Assert.that(scm instanceof ScmGit,  "Wrong Type at " + gitUrl);
        }
    }

    @Test
    public void getSVNFromScmFactory() throws Exception {
        Scm scm;
        scm = new ScmFactory(svn).get();
        Assert.that(scm instanceof ScmSVN, "Wrong type at SVN");
    }

    @Test
    public void getPomfromSCMS() throws Exception {
        getPom(svn);
        getPom(git);
    }

    private void getPom(String scmLocation) {
        Scm scm;
        scm = new ScmFactory(scmLocation).get();

        Assert.that(scm.getPom().length() > 1, "cannot parse pom");
    }

    @Test(expected = JobProfileException.class)
    public void WrongSVNUrl() throws Exception{
        new ScmFactory(svn + "/").get().getPom();
    }

    @Test(expected = JobProfileException.class)
    public void WrongGitUrl() throws  Exception{
        new ScmFactory("http:ajdlfjlsdjfö.git").get().getPom();
    }

    @Test(expected = JobProfileException.class)
    public void GitRepoWithoutPom() throws Exception {
        new ScmFactory("git@github.com:maxbraun/puppet-phantomjs.git").get().getPom();
    }
}

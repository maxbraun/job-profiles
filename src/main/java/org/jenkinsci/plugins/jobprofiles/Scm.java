package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

abstract class Scm {
    public static Scm get(String uri, World world) {
        Scm scm;
        final String gitPattern = "^(.*\\.git)[/]?$";

        if (uri.matches(gitPattern)) {
            scm = new ScmGit(uri, world);
            return scm;
        }

        return new ScmSVN("svn:" + uri, world);
    }

    abstract String getPom();

    abstract Map<String, String> getProfile(String name) throws IOException;

    abstract List<Node> find(String seachString);

    abstract Node findOne(String seachString);
}

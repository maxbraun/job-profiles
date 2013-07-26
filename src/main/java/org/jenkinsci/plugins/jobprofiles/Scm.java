package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.World;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public abstract class Scm {
    public static Scm get(String uri, World world) {
        Scm scm;
        final String gitPattern = "^(.*\\.git)[/]?$";

        if (uri.matches(gitPattern)) {
            scm = ScmGit.create(world, uri);
            return scm;
        }

        return ScmNode.create(world, "svn:" + uri);
    }

    public abstract String getPom() throws IOException;

    public boolean profileExists(String name, PrintStream log) throws IOException {
        return getProfile(name, log) != null;
    }

    public abstract Map<String, String> getProfile(String name, PrintStream log) throws IOException;

    public abstract List<Node> find(String seachString) throws IOException;

    public abstract Node findOne(String seachString) throws IOException;
}

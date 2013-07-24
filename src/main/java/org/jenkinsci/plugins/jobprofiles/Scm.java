package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.file.FileNode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface Scm {
    String getPom();

    Map<String, String> getProfile(String name) throws IOException;

    List<Node> find(String seachString);

    Node findOne(String seachString);
}

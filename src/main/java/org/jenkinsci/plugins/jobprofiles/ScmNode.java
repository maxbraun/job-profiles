package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScmNode extends Scm {

    public static Scm create(World world, String scm) {
        try {
            return new ScmNode(world.node(scm));
        } catch (URISyntaxException e) {
            throw new JobProfileException(e.getMessage(), e);
        } catch (NodeInstantiationException e) {
            throw new JobProfileException(e.getMessage(), e);
        }
    }

    private Node root;

    public ScmNode(Node root) {
        this.root = root;
    }


    public String getPom() {
        try {
            return root.findOne("pom.xml").toString();
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e);
        }
    }

    public Map<String, String> getProfile(String name, PrintStream log) throws IOException {
        Map<String, String> profiles;
        profiles = new HashMap<String, String>();

        for (Node file : root.join(name).list()) {
            profiles.put(file.getName(), file.toString());
        }

        return profiles;
    }

    public List<Node> find(String seachString) throws IOException {
        return root.find(seachString);
    }

    public Node findOne(String seachString) throws IOException {
        return root.findOne(seachString);
    }
}

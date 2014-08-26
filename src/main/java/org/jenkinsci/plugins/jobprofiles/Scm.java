package org.jenkinsci.plugins.jobprofiles;


import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.ToString;
import net.oneandone.sushi.fs.DirectoryNotFoundException;
import net.oneandone.sushi.fs.FileNotFoundException;
import net.oneandone.sushi.fs.ListException;
import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;

@ToString
public class Scm {
    private static String remote;

    public static Scm create(String scm, World world) {
        remote = scm;
        try {
            return new Scm(world.node("svn:" + remote));
        } catch (URISyntaxException e) {
            throw new JobProfileException(e.getMessage(), e);
        } catch (NodeInstantiationException e) {
            throw new JobProfileException(e.getMessage(), e);
        }
    }

    private Node root;

    public Scm(Node root) {
        this.root = root;
    }


    public String getPom() {
        try {
            return root.findOne("pom.xml").readString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e);
        }
    }

    public Map<String, String> getProfile(String name, PrintStream log) {
        Map<String, String> profiles;
        profiles = new HashMap<String, String>();
        try {
            for (Node file : root.join(name).list()) {
                profiles.put(file.getName(), file.readString());
            }
        } catch (ListException e) {
            throw new JobProfileException(e);
        } catch (DirectoryNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new JobProfileException(e);
        }

        return profiles;
    }

    public List<Node> find(String seachString) throws IOException {
        try {
            return root.find(seachString);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public Node findOne(String seachString) throws IOException {
        try {
            return root.findOne(seachString);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public String getRemote() {
        return remote;
    }
}

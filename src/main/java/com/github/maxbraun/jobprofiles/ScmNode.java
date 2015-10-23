package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNRevision;

import net.oneandone.sushi.fs.ExistsException;
import net.oneandone.sushi.fs.FileNotFoundException;
import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.svn.SvnNode;

public class ScmNode {
    private SvnNode root;

    public ScmNode(SvnNode root) {
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

    public boolean exists() throws ExistsException {
        return root.exists();
    }

    public boolean active() throws SVNException {
        DateTime date;
        date = new DateTime(root.getRoot().getRepository().info(root.getPath(), SVNRevision.HEAD.getNumber()).getDate().getTime());
        return date.isAfter(date.minus(Months.ONE));
    }


    public String category() {
        String category;
        if (root.getName().equals("trunk")) {
            category = root.getParent().getName();
            if (root.getParent().getParent() != null) {
                category = root.getParent().getParent().getName();
            }
        } else if (root.getParent().getName().equals("branches")) {
            if (root.getParent().getParent().getParent() != null) {
                category = root.getParent().getParent().getParent().getName();
            } else {
                category = "uncategorized";
            }
        } else {
            category = "cannot categorize";
        }
        return category;
    }

    public String uri() {
        return root.getURI().toString();
    }

    public String toString() {
        return "org.jenkinsci.plugins.jobprofiles.Scm(root=" + this.root + ")";
    }

    public World world() {
        return root.getWorld();
    }
}

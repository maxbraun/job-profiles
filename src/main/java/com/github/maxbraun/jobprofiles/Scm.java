package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;

import hudson.model.Item;
import hudson.scm.CredentialsSVNAuthenticationProviderImpl;
import hudson.scm.SubversionSCM;
import hudson.security.ACL;
import net.oneandone.sushi.fs.DirectoryNotFoundException;
import net.oneandone.sushi.fs.ExistsException;
import net.oneandone.sushi.fs.FileNotFoundException;
import net.oneandone.sushi.fs.ListException;
import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.svn.SvnFilesystem;
import net.oneandone.sushi.fs.svn.SvnNode;
import net.oneandone.sushi.util.Strings;

public class Scm {
    private SvnNode root;

    public static Scm create(String scm, World world, PrintStream log) {
        String remote;
        remote = Strings.removeRightOpt(scm, "/");
        if (remote.startsWith("scm:")) {
            remote = Strings.removeLeft(remote, "scm:");
        }
        if (!remote.startsWith("svn:")) {
            remote = "svn:" + remote;
        }
        try {
            URI remoteUri = new URI(remote);


            List<DomainRequirement> domainRequirements = URIRequirementBuilder.fromUri(remote).build();
            Item item = null;
            StandardUsernamePasswordCredentials credentials = CredentialsMatchers.firstOrNull(
              CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, item, ACL.SYSTEM, domainRequirements),
              CredentialsMatchers.always());


            SvnFilesystem svnFilesystem = (SvnFilesystem) world.getFilesystem("svn");

            if (credentials == null) {
                log.append("cannot get credentials for ").append(scm).append(" using none.");
            } else {
                ISVNAuthenticationManager svnAuthenticationManager =
                  SubversionSCM.createSvnAuthenticationManager(new CredentialsSVNAuthenticationProviderImpl(credentials));
                svnFilesystem.setDefaultAuthenticationManager(svnAuthenticationManager);
            }

            SvnNode node = svnFilesystem.node(remoteUri, null);
            if (node.isFile()) {
                return new Scm(node.getParent());
            } else {
                return new Scm(node);
            }
        } catch (URISyntaxException e) {
            throw new JobProfileException(e.getMessage(), e);
        } catch (NodeInstantiationException e) {
            throw new JobProfileException(e.getMessage(), e);
        } catch (ExistsException e) {
            throw new JobProfileException(e.getMessage(), e);
        }

    }
    public Scm(SvnNode root) {
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
    public boolean profileExists(String name, PrintStream log) throws IOException {
        return getProfile(name, log) != null;
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
}

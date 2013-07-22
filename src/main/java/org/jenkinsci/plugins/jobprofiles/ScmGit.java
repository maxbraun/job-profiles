package org.jenkinsci.plugins.jobprofiles;


import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.DeleteException;
import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.NodeNotFoundException;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ScmGit implements Scm {
    private final String scm;

    public ScmGit(String scm) {
        this.scm = scm;
    }

    public String getPom() {
        World world;
        FileNode localPath;

        world = new World();
        try {
            localPath = world.getTemp().createTempDirectory();
        } catch (IOException e) {
            throw new JobProfileException("Could not create temp directory.", e.getCause());
        }
        log.debug("Using " + localPath.toString());

        try {
            Git.cloneRepository()
                    .setDirectory(new File(localPath.getAbsolute())).setURI(scm)
                    .call();
            return localPath.findOne("pom.xml").readString();
        } catch (GitAPIException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } finally {
            try {
                localPath.deleteTree();
            } catch (DeleteException e) {
                log.error("Cannot delete Tempdir. {}", e.getMessage());
            } catch (NodeNotFoundException e) {
                log.error("Cannot delete Tempdir. {}", e.getMessage());
            }
        }
    }

    public Map<String, String> getProfile(String name) throws IOException {
        World world;
        world = new World();
        FileNode localPath;
        HashMap<String, String> profileMap;
        Node theProfile;

        localPath = world.getTemp().createTempDirectory();

        try {
            Git.cloneRepository()
                    .setDirectory(new File(localPath.getAbsolute())).setURI(scm)
                    .call();
        } catch (GitAPIException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        }

        profileMap = new HashMap<String, String>();
        theProfile = localPath.findOne(name);

        for (Node profileNode : theProfile.list()) {
            profileMap.put(profileNode.getName(), profileNode.readString());
        }
        return profileMap;
    }
}

package org.jenkinsci.plugins.jobprofiles;


import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.DeleteException;
import net.oneandone.sushi.fs.NodeNotFoundException;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

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
}

package org.jenkinsci.plugins.jobprofiles;


import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ScmGit implements Scm {

    public String getPom(String scm) {
        World world;
        FileNode localPath;
        Git git;

        world = new World();
        try {
            localPath = world.getTemp().createTempDirectory();
        } catch (IOException e) {
            ScmGit.log.error("Could not create temp directory.");
            return "";
        }
        log.debug("Using " + localPath.toString());

        try {
            git = Git.cloneRepository()
                    .setDirectory(new File(localPath.getAbsolute())).setURI(scm)
                    .call();
            return localPath.findOne("pom.xml").readString();
        } catch (GitAPIException e) {
            log.error("An error occured {}", e.getMessage());
            return "";
        } catch (IOException e) {
            log.error("An error occured {}", e.getMessage());
            return "";
        }
    }
}

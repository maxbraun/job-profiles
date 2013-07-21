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

    final String scm;

    public ScmGit(final String scm) {
        this.scm = scm;
    }

    public String getPom() {
        final World world;
        final FileNode localPath;
        Git git;

        world = new World();
        try {
            localPath = world.getTemp().createTempDirectory();
        } catch (IOException e) {
            throw new JobProfileException("Could not create temp directory.", e.getCause());
        }
        log.debug("Using " + localPath.toString());

        try {
            git = Git.cloneRepository()
                    .setDirectory(new File(localPath.getAbsolute())).setURI(scm)
                    .call();
            return localPath.findOne("pom.xml").readString();
        } catch (GitAPIException e) {
            throw new JobProfileException("Cannot checkout repository ", e.getCause());
        } catch (IOException e) {
            throw new JobProfileException("Cannot find pom.xml", e.getCause());
        }
    }
}

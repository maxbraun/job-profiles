package org.jenkinsci.plugins.jobprofiles;


import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ScmGit extends Scm {
    private final String scm;
    public final FileNode localpath;
    private final Git git;
    private final World world;

    public ScmGit(String scm, World world) {
        this.world = world;
        this.scm = scm;
        try {
            this.localpath = world.getTemp().createTempDirectory();
            git = Git.cloneRepository()
                    .setDirectory(new File(localpath.getAbsolute()))
                    .setURI(scm)
                    .call();

        } catch (GitAPIException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new JobProfileException("Could not create temp directory.", e.getCause());
        }

    }

    public String getPom() {
        try {

            return localpath.findOne("pom.xml").readString();

        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        }

    }


    public Map<String, String> getProfile(String name) throws IOException {
        HashMap<String, String> profileMap;
        Node theProfile;

        profileMap = new HashMap<String, String>();
        theProfile = localpath.findOne(name);

        for (Node profileNode : theProfile.list()) {
            profileMap.put(profileNode.getName(), profileNode.readString());
        }
        return profileMap;
    }

    public List<Node> find(String seachString) {
        try {
            return localpath.find(seachString);
        } catch (NodeInstantiationException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Node findOne(String seachString) {
        try {
            return localpath.findOne(seachString);
        } catch (NodeInstantiationException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}

package org.jenkinsci.plugins.jobprofiles;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class ProfileGit {

    @NonNull
    private String rootUri;
    @NonNull
    private String profile;

    public HashMap<String, String> getProfiles() throws IOException, GitAPIException {
        Git git;
        World world;
        world = new World();
        FileNode localPath;
        HashMap<String, String> profileMap;
        Node theProfile;

        localPath = world.getTemp().createTempDirectory();

        git = Git.cloneRepository()
                .setDirectory(new File(localPath.getAbsolute())).setURI(rootUri)
                .call();

        profileMap = new HashMap<String, String>();
        theProfile = localPath.findOne(profile);

        for (Node profileNode : theProfile.list()) {
            profileMap.put(profileNode.getName(), profileNode.readString());
        }
        return profileMap;
    }
}

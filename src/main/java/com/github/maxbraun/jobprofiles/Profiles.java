package com.github.maxbraun.jobprofiles;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.oneandone.sushi.fs.Node;
public class Profiles {

    public static Profiles fromNode(Node rootNode) throws IOException {
        rootNode.checkExists();
        Profiles profiles = new Profiles();
        for (Node directory : rootNode.list()) {
            if (directory.isDirectory()) {
                profiles.add(Profile.fromDirectory(directory));
            }
        }

        return profiles;
    }


    private final Map<String, Profile> profiles = new HashMap<String, Profile>();

    private void add(Profile profile) {
        profiles.put(profile.name(), profile);
    }

    public boolean exists(String profileName) {
        return profiles.containsKey(profileName);
    }

    public Profile get(String profileName) {
        return profiles.get(profileName);
    }

    public int size(){
        return profiles.size();
    }
}

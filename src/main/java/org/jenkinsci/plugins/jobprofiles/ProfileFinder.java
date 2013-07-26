package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.World;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileFinder {
    private final Scm profileRoot;
    private Scm assetScm;

    public ProfileFinder(String profileRootDir, World world) {
        this.profileRoot = Scm.get(profileRootDir, world);
    }

    public ProfileFinder setAssetSCM(Scm assetScm) {
        this.assetScm = assetScm;
        return this;
    }


    public String find() throws IOException {
        Map<String, String> buildSystems;

        buildSystems = new HashMap<String, String>();

        buildSystems.put("maven", "pom.xml");
        buildSystems.put("composer", "composer.json");

        for (Map.Entry system : buildSystems.entrySet()) {
            if (assetScm.findOne(system.getValue().toString()) != null) {
                return system.getKey().toString();
            }
        }

        return null;
    }


    public Map<String, String> getProfile(PrintStream log) throws IOException {
        String scm;

        scm = assetScm.getClass().getSimpleName().replace("Scm", "");
        return profileRoot.getProfile(String.format("%s-%s", scm, find()).toLowerCase(), log);
    }

    public Map<String, String> getProfile(String name, PrintStream log) throws IOException {
        return profileRoot.getProfile(name, log);
    }


}

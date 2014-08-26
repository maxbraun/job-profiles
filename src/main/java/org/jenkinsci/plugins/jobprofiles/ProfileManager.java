package org.jenkinsci.plugins.jobprofiles;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Lists;

import net.oneandone.sushi.fs.World;

/**
 * Manager Instance, which holds a list of profiles
 */
public class ProfileManager {
    private final Scm profileScm;
    private final PrintStream log;
    public Map<String, Profile> profileList;
    private World world;
    private ProfileFinder finder;

    public ProfileManager(World world, PrintStream log, String profileRoot) {
        this.profileScm = Scm.create(profileRoot, world);
        this.world = world;
        this.log = log;
        this.profileList = new HashMap<String, Profile>();
    }

    public Profile getProfileForScm(Scm projectSCM, String forcedProfile) throws IOException {
        discover(projectSCM, forcedProfile);
        return getProfile();
    }

    private ProfileManager discover(Scm projectSCM, String forcedProfile) throws IOException {
        finder = ProfileFinder.find(world, projectSCM, this.profileScm, forcedProfile);
        return this;
    }

    private String find() throws IOException {
        return finder.possibleProfiles.get(finder.possibleProfiles.size() - 1);
    }

    private Profile getProfile() throws IOException {

        for (String profileName : Lists.reverse(finder.possibleProfiles)) {

            if (finder.profileRoot.profileExists(profileName, log)) {

                if (profileList.containsKey(profileName)) {
                    return profileList.get(profileName);
                }

                Profile newProfile;
                newProfile = new Profile();
                newProfile.setName(profileName);
                newProfile.setXmls(finder.profileRoot.getProfile(profileName, log));
                profileList.put(profileName, newProfile);
                return newProfile;
            }

        }
        return null;
    }
}

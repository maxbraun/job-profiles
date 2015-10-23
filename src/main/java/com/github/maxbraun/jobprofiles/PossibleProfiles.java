package com.github.maxbraun.jobprofiles;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.project.MavenProject;

import jenkins.model.Jenkins;

/**
 * Heuristic to find a Profile
 */
public class PossibleProfiles {


    private final static String STANDARD = "implicit";
    private final static String JENKINS_FILE = ".jenkins";

    private final MavenProjectResolver mavenProjectResolver;

    public PossibleProfiles(MavenProjectResolver resolver) {
        this.mavenProjectResolver = resolver;
    }

    public List<String> of(SoftwareAsset asset, String forcedProfile) throws IOException {
        List<String> possibleProfiles = new ArrayList<String>();
        if (forcedProfile != null && !forcedProfile.isEmpty()) {
            possibleProfiles.add(forcedProfile);
            return possibleProfiles;
        }
        String buildSystem = findBuildSystem(asset.scmNode());
        if (buildSystem != null) {
            possibleProfiles.add(buildSystem);
        }
        if (asset.scmNode().getPom() != null && Jenkins.getInstance() != null) {
            String mavenProperty = findMavenProperty(mavenProjectResolver.resolveFrom(asset.scmNode().getPom()));
            if (mavenProperty != null) {
                possibleProfiles.add(mavenProperty);
            }
        }
        String jenkinsFile = findJenkinsFile(asset.scmNode());
        if (jenkinsFile != null) {
            possibleProfiles.add(jenkinsFile);
        }

        return possibleProfiles;
    }


    private String findJenkinsFile(ScmNode scm) throws IOException {
        Properties properties;
        if (scm.findOne(JENKINS_FILE) != null) {
            properties = scm.findOne(JENKINS_FILE).readProperties();
            return properties.getProperty("profile");
        }
        return null;
    }
    private String findBuildSystem(ScmNode scm) throws IOException {
        Map<String, String> buildSystems;

        buildSystems = new HashMap<String, String>();

        buildSystems.put("maven", "pom.xml");
        buildSystems.put("composer", "composer.json");

        for (Map.Entry<String, String> system : buildSystems.entrySet()) {
            if (scm.findOne(system.getValue()) != null) {
                return system.getKey();
            }
        }
        return null;
    }
    private String findMavenProperty(MavenProject project) throws IOException {
        if (project.getProperties() != null) {
            return project.getProperties().getProperty("jenkins.profile");
        }
        return null;
    }



}

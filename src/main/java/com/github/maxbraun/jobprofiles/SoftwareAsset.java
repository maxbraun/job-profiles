package com.github.maxbraun.jobprofiles;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang.NotImplementedException;
import org.apache.maven.project.MavenProject;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.util.Strings;


public class SoftwareAsset {

    public static SoftwareAsset fromSCM(String scmLocation, World world, PrintStream log) throws IOException {
        Scm scm = Scm.create(scmLocation, world, log);

        String artifactId;
        //TODO: I need some kind of a Builder with several Impls for each build system.
        if (scm.getPom() == null) {
            throw new NotImplementedException("Can't read something else then maven projects - currently sorry");
        }
        MavenProject mavenProject = ContextBuilder.getMavenProject(scm.getPom(), world);
        artifactId = mavenProject.getArtifactId();
        if (!scmLocation.endsWith("trunk")) {
            artifactId = mavenProject.getArtifactId() + " " + scmLocation.substring(scmLocation.lastIndexOf('/') + 1);
        }

        return new SoftwareAsset(scmLocation, new Coordinates(artifactId, mavenProject.getGroupId()));

    }
    @JsonProperty
    private String origin;
    @JsonProperty
    private Coordinates coordinates;

    public SoftwareAsset(String origin, Coordinates coordinates) {
        this.origin = origin;
        this.coordinates = coordinates;
    }
    public SoftwareAsset() {
    }

    public String artifactId() {
        return coordinates.getArtifactId();
    }

    public String groupId() {
        return coordinates.getGroupId();
    }

    public String scm() {
        return Strings.removeLeftOpt(this.origin, "svn:");
    }

    @Override
    public String toString() {
        return "SoftwareAsset{" +
          "origin='" + origin + '\'' +
          ", coordinates=" + coordinates +
          '}';
    }
}

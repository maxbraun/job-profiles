package com.github.maxbraun.jobprofiles;
import java.io.IOException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.maven.project.MavenProject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.oneandone.sushi.util.Strings;


public class SoftwareAsset {

    public static SoftwareAsset fromSCM(String scmLocation) throws IOException {
        ScmNode scm = new ScmNode(new SvnNodeBuilder(scmLocation).build()) ;

        String artifactId;
        if (scm.getPom() == null) {
            throw new NotImplementedException("Can't read something else then maven projects - currently sorry");
        }

        MavenProject mavenProject = new MavenProjectResolver(scm.world()).resolveFrom(scm.getPom());
        artifactId = mavenProject.getArtifactId();

        if (!scmLocation.endsWith("trunk")) {
            artifactId = mavenProject.getArtifactId() + " " + scmLocation.substring(scmLocation.lastIndexOf('/') + 1);
        }

        return new SoftwareAsset(scmLocation, new Coordinates(artifactId, mavenProject.getGroupId()), scm);

    }
    @JsonProperty
    private String origin;
    @JsonProperty
    private Coordinates coordinates;

    @JsonIgnore
    private ScmNode scm;

    public SoftwareAsset(String origin, Coordinates coordinates, ScmNode scmNode) {
        this.origin = origin;
        this.coordinates = coordinates;
        this.scm = scmNode;
    }
    public SoftwareAsset() {
    }

    public String artifactId() {
        return coordinates.getArtifactId();
    }

    public String groupId() {
        return coordinates.getGroupId();
    }

    public String origin() {
        return Strings.removeLeftOpt(this.origin, "svn:");
    }


    public ScmNode scmNode() {
        if (origin == null) {
            return null;
        }

        if (scm == null) {
            scm = new ScmNode(new SvnNodeBuilder(origin).build());
        }
        return scm;
    }

    @Override
    public String toString() {
        return "SoftwareAsset{" +
          "origin='" + origin + '\'' +
          ", coordinates=" + coordinates +
          '}';
    }
}

package com.github.maxbraun.jobprofiles;
import java.io.IOException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.maven.project.MavenProject;

import net.oneandone.pommes.model.Pom;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.util.Strings;

public class SoftwareAsset {
    private String artifactId;
    private String groupId;
    private String category;
    private String scm;
    private boolean active = true;
    public SoftwareAsset() {
    }
    public SoftwareAsset(String groupId, String artifactId, String scm, String category) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.scm = scm;
        this.category = category;
    }

    public static SoftwareAsset withPom(Pom pom, boolean active, String category) {
        SoftwareAsset asset;
        asset = new SoftwareAsset();
        asset.artifactId = pom.artifactId;
        asset.groupId = pom.groupId;
        asset.scm = Strings.removeRightOpt(Strings.removeLeftOpt(pom.scm, "scm:"), "/");
        asset.active = active;
        asset.category = category;
        return asset;

    }

    public static SoftwareAsset fromSCM(String scmLocation, World world) throws IOException {
        Scm scm = Scm.create(scmLocation, world);
        SoftwareAsset asset;
        asset = new SoftwareAsset();
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
        asset.artifactId = artifactId;
        asset.groupId = mavenProject.getGroupId();
        asset.scm = scmLocation;
        return asset;
    }
    public String artifactId() {
        return this.artifactId;
    }

    public boolean active() {
        return active;
    }

    public String groupId() {
        return this.groupId;
    }

    public String category() {
        return this.category;
    }

    public String scm() {
        return this.scm;
    }


    public String toString() {
        return "SoftwareAsset(artifactId=" + this.artifactId + ", groupId=" + this.groupId + ", category=" + this.category + ", scm=" + this.scm + ")";
    }
}

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
    public SoftwareAsset() {
    }

    public static SoftwareAsset withPom(Pom pom) {
        SoftwareAsset asset;
        asset = new SoftwareAsset();
        asset.setArtifactId(pom.artifactId);
        asset.setGroupId(pom.groupId);
        asset.setScm(Strings.removeRightOpt(Strings.removeLeftOpt(pom.scm, "scm:"), "/"));
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
            artifactId = mavenProject.getArtifact() + " " + scmLocation.substring(scmLocation.lastIndexOf('/'));
        }
        asset.setArtifactId(artifactId);
        asset.setGroupId(mavenProject.getGroupId());
        asset.setScm(scmLocation);
        return asset;
    }
    public String artifactId() {
        return this.artifactId;
    }
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    public String groupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String category() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String scm() {
        return this.scm;
    }
    public void setScm(String scm) {
        this.scm = scm;
    }


    public String toString() {
        return "SoftwareAsset(artifactId=" + this.artifactId + ", groupId=" + this.groupId + ", category=" + this.category + ", scm=" + this.scm + ")";
    }
}

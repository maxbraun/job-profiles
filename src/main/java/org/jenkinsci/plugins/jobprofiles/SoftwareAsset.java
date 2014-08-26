package org.jenkinsci.plugins.jobprofiles;

import java.io.IOException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.maven.project.MavenProject;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.oneandone.sushi.fs.World;

@Data
@NoArgsConstructor
@XStreamAlias("asset")
public class SoftwareAsset {
    private String id;
    private String artifactId;
    private String groupId;
    private String category;
    private String trunk;
    private String type;

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
        asset.setTrunk(scmLocation);
        asset.setId("0");
        return asset;
    }
}

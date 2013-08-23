package org.jenkinsci.plugins.jobprofiles;

import hudson.maven.MavenEmbedder;
import hudson.maven.MavenEmbedderException;
import hudson.maven.MavenUtil;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.tasks.Maven;
import hudson.util.LogTaskListener;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Context {
    public static Map<String, Object> get(SoftwareAsset asset, Scm scm, World world, AbstractBuild build) throws IOException {
        Map<String, Object> context = new HashMap<String, Object>();

        context.put("scm", asset.getTrunk());
        context.put("version", "");
        context.put("now", new Date(build.getTimestamp().getTimeInMillis()).toString());

        // do a lookup in the Index…
        asset.setCategory(asset.getCategory() == null ? "No Category" : asset.getCategory());
        asset.setId(asset.getId() == null ? "Freestyle" : asset.getId());
        context.put("category", asset.getCategory() == null ? "No Category" : asset.getCategory());
        context.put("asset", asset);

        return getMavenContext(context, world, scm);
    }

    public static Map<String, Object> getMavenContext(Map<String, Object> context, World world, Scm scm) throws IOException {
        MavenProject project;
        String pom;

        pom = scm.getPom();

        if (pom == null) return context;
        project = getMavenProject(pom, world);


        context.put("mavenproject", project);
        context.put("name", project.getArtifactId());
        context.put("identifier", project.getGroupId());

        for (Map.Entry entry : project.getProperties().entrySet()) {
            context.put(entry.getKey().toString().replace(".", "_"), entry.getValue());
        }

        return context;
    }


    public static MavenProject getMavenProject(String pomContent, World world) {
        MavenEmbedder embedder;
        MavenProject mavenProject;
        FileNode tmpPom;
        File mavenHome;
        Maven.MavenInstallation[] installations;
        TaskListener listener;

        installations = Jenkins.getInstance().getDescriptorByType(hudson.tasks.Maven.DescriptorImpl.class).getInstallations();

        if (installations.length == 0) {
            throw new JobProfileException("No Maven installation found.");
        }

        mavenHome = installations[0].getHomeDir();
        listener = new LogTaskListener(Logger.getLogger(Context.class.toString()), Level.ALL);

        try {
            tmpPom = (FileNode) world.getTemp().createTempFile().writeStrings(pomContent);
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        }

        try {
            assert tmpPom != null;
            embedder = MavenUtil.createEmbedder(listener, mavenHome, null);
            mavenProject = embedder.readProject(new File(tmpPom.toString()));
        } catch (MavenEmbedderException e) {
            throw new JobProfileException(e.getMessage(), e);
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e);
        } catch (ProjectBuildingException e) {
            throw new JobProfileException(e.getMessage(), e);
        }
        assert mavenProject != null;
        return mavenProject;
    }

}

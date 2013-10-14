package org.jenkinsci.plugins.jobprofiles;

import hudson.maven.MavenEmbedder;
import hudson.maven.MavenEmbedderException;
import hudson.maven.MavenUtil;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextBuilder {
    public static void add(Job job, World world, SoftwareAsset asset) throws IOException {
        addMavenContext(job, world);
        addJenkinsContext(job);
        addSoftwareAssetContext(job, asset);
    }

    public static void addSoftwareAssetContext(Job job, SoftwareAsset asset) {
        job.addContext("name", asset.getArtifactId());
        job.addContext("identifier", asset.getGroupId());
    }

    public static void addJenkinsContext(Job job) throws IOException {
        job.addContext("max_executors", Jenkins.getInstance().getNumExecutors());
    }

    public static void addMavenContext(Job job, World world) throws IOException {
        MavenProject project;
        String pom;
        if (job.getScm() == null ) return;
        pom = job.getScm().getPom();

        if (pom == null) return;
        project = getMavenProject(pom, world);


        job.addContext("mavenproject", project);

        for (Map.Entry entry : project.getProperties().entrySet()) {
            job.addContext(entry.getKey().toString().replace(".", "_"), entry.getValue());
        }
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
            throw new JobProfileException(Messages.Context_NoMavenInstallation());
        }

        mavenHome = installations[0].getHomeDir();
        listener = new LogTaskListener(Logger.getLogger(ContextBuilder.class.toString()), Level.ALL);

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

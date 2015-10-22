package com.github.maxbraun.jobprofiles;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;

import hudson.maven.MavenEmbedder;
import hudson.maven.MavenEmbedderException;
import hudson.maven.MavenEmbedderRequest;
import hudson.maven.MavenUtil;
import hudson.model.TaskListener;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.tasks.Maven;
import hudson.util.LogTaskListener;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;

public class ContextBuilder {
    public static void add(Job job, World world, SoftwareAsset asset, PrintStream log) throws IOException {
        addMavenContext(job, world, log);
        addJenkinsContext(job);
        addSoftwareAssetContext(job, asset);
    }

    public static void addSoftwareAssetContext(Job job, SoftwareAsset asset) {
        job.addContext("name", asset.artifactId());
        job.addContext("identifier", asset.groupId());
        job.addContext("disabled", false);
    }

    public static void addJenkinsContext(Job job) throws IOException {
        job.addContext("max_executors", Jenkins.getInstance().getNumExecutors());
    }

    public static void addMavenContext(Job job, World world, PrintStream log) throws IOException {
        MavenProject project;
        String pom;
        if (job.scm() == null) {
            return;
        }
        pom = job.scm().getPom();

        if (pom == null) {
            return;
        }
        project = getMavenProject(pom, world, log);


        job.addContext("mavenproject", project);

        for (Map.Entry entry : project.getProperties().entrySet()) {
            job.addContext(entry.getKey().toString().replace(".", "_"), entry.getValue());
        }
    }


    public static MavenProject getMavenProject(String pomContent, World world, PrintStream log) {
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
            assert tmpPom != null;
            embedder = MavenUtil.createEmbedder(new MavenEmbedderRequest(listener, mavenHome, null, getEnvironmentVariables(), null, null));
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

    private static Properties getEnvironmentVariables() {
        Properties properties;
        EnvironmentVariablesNodeProperty environmentVariablesNodeProperty;

        properties = new Properties();
        environmentVariablesNodeProperty = Jenkins.getInstance().getGlobalNodeProperties().get(EnvironmentVariablesNodeProperty.class);

        if (environmentVariablesNodeProperty != null) {
            for (Map.Entry<String, String> entry : environmentVariablesNodeProperty.getEnvVars().entrySet()) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
        }

        return properties;
    }

}

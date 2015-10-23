package com.github.maxbraun.jobprofiles;
import java.io.File;
import java.io.IOException;
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
public class MavenProjectResolver {

    private final World world;
    private final TaskListener listener = new LogTaskListener(Logger.getLogger(JobContextBuilder.class.toString()), Level.ALL);

    public MavenProjectResolver(World world) {
        this.world = world;
    }

    public MavenProject resolveFrom(String pomContent) {
        if (pomContent == null || pomContent.isEmpty()) {
            throw new IllegalArgumentException("pomContent is null or empty");
        }
        MavenEmbedder embedder;
        MavenProject mavenProject;
        FileNode tmpPom;
        File mavenHome;
        Maven.MavenInstallation[] installations;

        installations = Jenkins.getInstance().getDescriptorByType(hudson.tasks.Maven.DescriptorImpl.class).getInstallations();

        if (installations.length == 0) {
            throw new JobProfileException(Messages.Context_NoMavenInstallation());
        }
        mavenHome = installations[0].getHomeDir();
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

    private Properties getEnvironmentVariables() {
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

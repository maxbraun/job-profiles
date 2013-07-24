package org.jenkinsci.plugins.jobprofiles;


import hudson.maven.MavenEmbedder;
import hudson.maven.MavenEmbedderException;
import hudson.maven.MavenUtil;
import hudson.model.TaskListener;
import hudson.tasks.Maven;
import hudson.util.LogTaskListener;
import jenkins.model.Jenkins;
import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.file.FileNode;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
public class ContextMaven extends Context {
    private Map<String, Object> context;
    private final Scm scm;
    private World world;

    public ContextMaven(Scm scm, World world) {
        this.world = world;
        this.scm = scm;
        this.context = new HashMap<String, Object>();
    }

    public Map<String, Object> getContext() {
        this.createContext();
        return context;
    }

    public void createContext() {
        MavenProject project;

        project = getMavenProject(scm.getPom());

        context.put("mavenproject", project);
    }

    private MavenProject getMavenProject(String pomContent) {
        MavenEmbedder embedder = null;
        MavenProject mavenProject = null;
        FileNode tmpPom = null;
        File mavenHome = null;
        Maven.MavenInstallation[] installations;
        TaskListener listener;

        installations = Jenkins.getInstance().getDescriptorByType(hudson.tasks.Maven.DescriptorImpl.class).getInstallations();

        if (installations.length == 0) {
            throw new JobProfileException("No Maven installation found.");
        }

        mavenHome = installations[0].getHomeDir();
        listener = new LogTaskListener(Logger.getLogger(ContextMaven.class.toString()), Level.ALL);

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
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (ProjectBuildingException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        }
        assert mavenProject != null;
        return mavenProject;
    }

}

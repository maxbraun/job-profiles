package com.github.maxbraun.jobprofiles;
import java.io.IOException;
import java.io.PrintStream;

import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import javaposse.jobdsl.plugin.ExecuteDslScripts;
import javaposse.jobdsl.plugin.RemovedJobAction;
import javaposse.jobdsl.plugin.RemovedViewAction;
import jenkins.model.Jenkins;

public class JobDslPluginJobSubmitter implements JobSubmitter {

    public static final String UPDATE_JOB = "JobDslUpdater";
    private final PrintStream log;

    public JobDslPluginJobSubmitter(PrintStream log) {
        this.log = log;
    }


    @Override
    public void submit(Job job) throws JobProfileException {
        TopLevelItem item = Jenkins.getActiveInstance().getItem(UPDATE_JOB);
        FreeStyleProject project;

        try {
            if (item == null || !(item instanceof FreeStyleProject)) {
                project = createProject();
            } else {
                project = (FreeStyleProject)item;
            }
        } catch (InterruptedException e){
            throw new JobProfileException(e);
        } catch (IOException e){
            throw new JobProfileException(e);
        }

        ExecuteDslScripts.ScriptLocation scriptLocation = new ExecuteDslScripts.ScriptLocation("true", null, job.getContent());
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts(scriptLocation, false, RemovedJobAction.IGNORE, RemovedViewAction.IGNORE, null);


        project.getBuildersList().add(executeDslScripts);



        try {
            project.save();
        } catch (IOException e){
            e.printStackTrace(log);
        }
    }

    private FreeStyleProject createProject() throws IOException, InterruptedException {
        FreeStyleProject project = new FreeStyleProject(Jenkins.getActiveInstance(), UPDATE_JOB);
        Jenkins.getActiveInstance().putItem(project);
        return project;
    }


}

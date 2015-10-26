package com.github.maxbraun.jobprofiles;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import javaposse.jobdsl.plugin.MonitorTemplateJobs;
import jenkins.model.Jenkins;
public class Jobs implements Iterable<Job> {

    private final List<Job> jobs = new ArrayList<Job>();

    @Override
    public Iterator<Job> iterator() {
        return jobs.iterator();
    }

    public void add(Job job) {
        jobs.add(job);
    }
    public void add(Jobs theirjobs) {
        for (Job job : theirjobs) {
            jobs.add(job);
        }
    }

    //TODO
    public void submit(PrintStream log) throws IOException {
        FreeStyleProject project = null;
        TopLevelItem item = Jenkins.getActiveInstance().getItem(JobDslPluginJobSubmitter.UPDATE_JOB);
        if (item != null && item instanceof FreeStyleProject) {
            project = ((FreeStyleProject) item);
            project.getBuildersList().clear();
            project.save();
        }
        for (Job job : jobs) {
            job.submit(log);
        }
        if (project != null) {
            project.scheduleBuild(new MonitorTemplateJobs.TemplateTriggerCause());
            log.println("Triggered " + project.getName());
        }

    }
}

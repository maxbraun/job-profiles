package com.github.maxbraun.jobprofiles;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public void submit(PrintStream log) {
        for (Job job : jobs) {
            job.submit(log);
        }

    }
}

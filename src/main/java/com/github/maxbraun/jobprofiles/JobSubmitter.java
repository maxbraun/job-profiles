package com.github.maxbraun.jobprofiles;
public interface JobSubmitter {
    void submit(Job job) throws JobProfileException;
}

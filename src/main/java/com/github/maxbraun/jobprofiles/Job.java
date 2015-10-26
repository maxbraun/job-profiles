package com.github.maxbraun.jobprofiles;

import java.io.PrintStream;
public class Job {

    private final String identifier;
    private final String content;
    private final TemplateType templateType;


    public Job(String identifier, String content, TemplateType templateType) {
        this.identifier = identifier;
        this.content = content;
        this.templateType = templateType;
    }

    public void submit(PrintStream log) {
        if (templateType.equals(TemplateType.XML)) {
            new XmlJobSubmitter(log).submit(this);
        } else if (templateType.equals(TemplateType.JOBDSL)) {
            new JobDslPluginJobSubmitter(log).submit(this);
        }
    }

    public String getIdentifier() {
        return identifier;
    }
    public String getContent() {
        return content;
    }
    public TemplateType getTemplateType() {
        return templateType;
    }
}

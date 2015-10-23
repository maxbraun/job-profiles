package com.github.maxbraun.jobprofiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import hudson.model.AbstractItem;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
public class Job {

    private final String identifier;
    private final String content;


    public Job(String identifier, String content) {
        this.identifier = identifier;
        this.content = content;
    }

    public void submit(PrintStream log) {
        InputStream src;
        src = new ByteArrayInputStream(content.getBytes());
        TopLevelItem item = Jenkins.getInstance().getItem(identifier);
        try{
            if (item == null) {
                Jenkins.getInstance()
                  .createProjectFromXML(identifier, src);
                log.println(String.format("Job %s created", identifier));
            } else {
                Source source = new StreamSource(src);
                if (!((AbstractItem) item).getConfigFile().toString().equals(content)) {
                    ((AbstractItem)item).updateByXml(source);
                    log.println(String.format("Job %s updated", identifier));
                }
            }

        } catch (IOException e) {
            log.println("could not parse because" + e.getMessage());
            log.println(content);
        } finally {
            try {
                src.close();
            } catch (IOException e){
                log.print(e);
            }
        }

    }
}

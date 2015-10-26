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
public class XmlJobSubmitter implements JobSubmitter {
    private final PrintStream log;
    public XmlJobSubmitter(PrintStream log) {
        this.log = log;
    }
    @Override
    public void submit(Job job) throws JobProfileException {
        InputStream src;
        src = new ByteArrayInputStream(job.getContent().getBytes());
        TopLevelItem item = Jenkins.getInstance().getItem(job.getIdentifier());
        try{
            if (item == null) {
                Jenkins.getInstance()
                  .createProjectFromXML(job.getIdentifier(), src);
                log.println(String.format("Job %s created", job.getIdentifier()));
            } else {
                Source source = new StreamSource(src);
                if (!((AbstractItem) item).getConfigFile().toString().equals(job.getContent())) {
                    ((AbstractItem)item).updateByXml(source);
                    log.println(String.format("Job %s updated", job.getIdentifier()));
                }
            }

        } catch (IOException e) {
            log.println("could not parse because" + e.getMessage());
            log.println(job.getContent());
        } finally {
            try {
                src.close();
            } catch (IOException e){
                log.print(e);
            }
        }
    }
}

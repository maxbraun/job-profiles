package org.jenkinsci.plugins.jobprofiles;


import com.thoughtworks.xstream.io.xml.DomDriver;
import hudson.util.XStream2;
import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.CreateInputStreamException;
import net.oneandone.sushi.fs.FileNotFoundException;
import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;

@Slf4j
public class Parser {

    public static SoftwareIndex parse(String datasource) throws NodeInstantiationException, FileNotFoundException, CreateInputStreamException {
        SoftwareIndex softwareIndex;
        World world;

        world = new World();

        XStream2 xstream;
        Node datasourceURI;

        datasourceURI = world.validNode(datasource);
        xstream = new XStream2(new DomDriver());

        xstream.autodetectAnnotations(true);
        xstream.alias("asset", SoftwareAssetImpl.class);
        xstream.alias("assets", XmlSoftwareIndex.class);
        return (SoftwareIndex) xstream.fromXML(datasourceURI.createInputStream());
    }
}

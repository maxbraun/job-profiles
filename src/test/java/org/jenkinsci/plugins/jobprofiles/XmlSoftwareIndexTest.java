package org.jenkinsci.plugins.jobprofiles;

import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.World;
import org.junit.Test;


@Slf4j
public class XmlSoftwareIndexTest {
    @Test
    public void getAssets() throws Exception {
        World world;
        SoftwareIndex foo;

        world = new World();

        foo = Parser.parse("svn:https://github.com/maxbraun/job-profiles/trunk/src/main/resources/softreg.xml");
        log.info(foo.toString());

    }


}

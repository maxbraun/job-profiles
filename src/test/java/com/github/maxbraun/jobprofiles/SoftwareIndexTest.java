package com.github.maxbraun.jobprofiles;
import org.junit.Test;

import net.oneandone.sushi.fs.World;
public class    SoftwareIndexTest {

    @Test
    public void testLoad() throws Exception {
        SoftwareIndex.load(new World().resource("index.json"));
    }
}
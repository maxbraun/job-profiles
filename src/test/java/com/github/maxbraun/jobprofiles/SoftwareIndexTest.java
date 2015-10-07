package com.github.maxbraun.jobprofiles;
import java.io.PrintStream;

import org.junit.Test;

import net.oneandone.sushi.fs.World;
public class    SoftwareIndexTest {

    @Test
    public void testLoad() throws Exception {
        SoftwareIndex.load(new World().resource("index.json"), new PrintStream(System.out));
    }
}
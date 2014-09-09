package com.github.maxbraun.jobprofiles;
import java.io.PrintStream;

import org.junit.Test;

import net.oneandone.sushi.fs.World;
public class SoftwareIndexTest {

    @Test
    public void testLoad() throws Exception {
        SoftwareIndex.load(new World().node("https://raw.githubusercontent.com/maxbraun/job-profiles/master/src/test/resources/index.zip"),
          new PrintStream(System.out));
    }
}
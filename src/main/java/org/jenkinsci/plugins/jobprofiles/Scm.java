package org.jenkinsci.plugins.jobprofiles;


import java.io.IOException;
import java.util.Map;

public interface Scm {
    String getPom();

    Map<String, String> getProfile(String name) throws IOException;
}

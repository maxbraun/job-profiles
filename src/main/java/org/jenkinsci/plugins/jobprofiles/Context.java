package org.jenkinsci.plugins.jobprofiles;

import java.util.Map;

public interface Context {
    Map<String, Object> getContext();

    void createContext();
}

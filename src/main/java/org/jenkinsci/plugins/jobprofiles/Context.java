package org.jenkinsci.plugins.jobprofiles;

import net.oneandone.sushi.fs.World;

import java.util.Map;

public abstract class Context {
    abstract Map<String, Object> getContext();

    abstract void createContext();

    public static Context get(Scm scm, World world) {
        return new ContextMaven(scm, world);
    }
}

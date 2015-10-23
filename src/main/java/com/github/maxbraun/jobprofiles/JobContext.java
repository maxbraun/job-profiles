package com.github.maxbraun.jobprofiles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class JobContext {

    private final Map<String, Object> context = new HashMap<String, Object>();

    protected void add(String identifier, Object data) {
        context.put(identifier, data);
    }

    public Map<String, Object> contextMap() {
        return Collections.unmodifiableMap(context);
    }
}

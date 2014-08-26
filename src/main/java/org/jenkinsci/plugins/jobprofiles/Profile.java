package org.jenkinsci.plugins.jobprofiles;


import java.util.Map;

import lombok.Data;

@Data
public class Profile {
    private String name;

    private Map<String, String> xmls;

}

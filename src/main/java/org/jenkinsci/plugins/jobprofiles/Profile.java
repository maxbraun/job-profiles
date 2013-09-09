package org.jenkinsci.plugins.jobprofiles;


import lombok.Data;

import java.util.Map;

@Data
public class Profile {
    private String name;

    private Map<String, String> xmls;

}

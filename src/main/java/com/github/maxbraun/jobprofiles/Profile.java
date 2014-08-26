package com.github.maxbraun.jobprofiles;

import java.util.Map;

public class Profile {
    private String name;

    private Map<String, String> xmls;
    public Profile() {
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Map<String, String> getXmls() {
        return this.xmls;
    }
    public void setXmls(Map<String, String> xmls) {
        this.xmls = xmls;
    }
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        final Profile other = (Profile) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$name = this.name;
        final Object other$name = other.name;
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        final Object this$xmls = this.xmls;
        final Object other$xmls = other.xmls;
        if (this$xmls == null ? other$xmls != null : !this$xmls.equals(other$xmls)) {
            return false;
        }
        return true;
    }
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.name;
        result = result * PRIME + ($name == null ? 0 : $name.hashCode());
        final Object $xmls = this.xmls;
        result = result * PRIME + ($xmls == null ? 0 : $xmls.hashCode());
        return result;
    }
    public boolean canEqual(Object other) {
        return other instanceof Profile;
    }
    public String toString() {
        return "org.jenkinsci.plugins.jobprofiles.Profile(name=" + this.name + ", xmls=" + this.xmls + ")";
    }
}

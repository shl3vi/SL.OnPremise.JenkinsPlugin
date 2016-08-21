package io.sealights.plugins.sealightsjenkins.integration.upgrade.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by shahar on 8/16/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentInfo {
    private String date;
    private String name;
    private String url;
    private String version;

    public AgentInfo() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

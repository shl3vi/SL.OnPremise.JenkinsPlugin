package io.sealights.plugins.sealightsjenkins.integration.upgrade.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by shahar on 8/16/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpgradeResponse {
    private AgentInfo agent;

    public UpgradeResponse(){
    }

    public AgentInfo getAgent() {
        return agent;
    }

    public void setAgent(AgentInfo agent) {
        this.agent = agent;
    }

}
package com.example.thingflowsdk.core.entity;

import rogo.iot.module.platform.dao.DaoEntity;
import rogo.iot.module.platform.dao.DaoField;

@DaoEntity(tableName = "flow_scenario")
public class TFFlowScenario {
    @DaoField
    private String uuid;
    @DaoField
    private String label;

    public TFFlowScenario() {
    }

    public TFFlowScenario(String label, String uuid) {
        this.label = label;
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

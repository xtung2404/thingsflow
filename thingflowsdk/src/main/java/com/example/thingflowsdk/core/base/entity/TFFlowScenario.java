package com.example.thingflowsdk.core.base.entity;

import rogo.iot.module.platform.dao.DaoEntity;
import rogo.iot.module.platform.dao.DaoField;

@DaoEntity(tableName = "flow_scenario")
public class TFFlowScenario {

    @DaoField
    private String uuid;
    @DaoField
    private String label;

    @DaoField
    private String desc;


    public TFFlowScenario() {
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

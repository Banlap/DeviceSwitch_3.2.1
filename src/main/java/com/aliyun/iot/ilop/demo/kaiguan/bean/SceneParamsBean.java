package com.aliyun.iot.ilop.demo.kaiguan.bean;

import java.io.Serializable;

public class SceneParamsBean  implements Serializable {
       private String iotId;
       private String propertyName;
       private Object propertyValue;
       private Integer delayedExecutionSeconds;

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Object propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Integer getDelayedExecutionSeconds() {
        return delayedExecutionSeconds;
    }

    public void setDelayedExecutionSeconds(Integer delayedExecutionSeconds) {
        this.delayedExecutionSeconds = delayedExecutionSeconds;
    }
}

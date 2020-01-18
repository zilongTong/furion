package org.furion.core.bean.eureka;

import lombok.Builder;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-17
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Builder
public class InstanceBean {

    @XmlElement(name = "hostName")
    private String hostName;
    @XmlElement(name = "instanceId")
    private String instanceId;
    @XmlElement(name = "app")

    private String app;
    @XmlElement(name = "port")

    private String port;
    //获取注册信息的最新时间
    @XmlElement(name = "lastUpdatedTimestamp")

    private Long lastUpdateTime;
    @XmlElement(name = "lastDirtyTimestamp")

    //实例的最后更新时间
    private Long lastDirtyTime;

    @XmlElement(name = "status")

    private String status;
    @XmlElement(name = "actionType")
    private String actionType;
    @XmlElement(name = "metadata")
    private MetaData metaData;

    public InstanceBean() {
    }

    public InstanceBean(String hostName, String instanceId, String app, String port, Long lastUpdateTime, Long lastDirtyTime, String status, String actionType, MetaData metaData) {
        this.hostName = hostName;
        this.instanceId = instanceId;
        this.app = app;
        this.port = port;
        this.lastUpdateTime = lastUpdateTime;
        this.lastDirtyTime = lastDirtyTime;
        this.status = status;
        this.actionType = actionType;
        this.metaData = metaData;
    }
}

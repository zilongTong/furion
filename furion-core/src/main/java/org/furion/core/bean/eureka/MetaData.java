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
 * @date 2019-12-16
 */
@Data
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaData {

    @XmlElement(name = "group")
    private String group;

    @XmlElement(name = "version")
    private String version;

    @XmlElement(name = "region")
    private String region;

    @XmlElement(name = "env")
    private String env;

    @XmlElement(name = "appId")
    private String appId;

    public MetaData() {
    }

    public MetaData(String group, String version, String region, String env, String appId) {
        this.group = group;
        this.version = version;
        this.region = region;
        this.env = env;
        this.appId = appId;
    }
}

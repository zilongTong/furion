package org.furion.core.bean.eureka;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-17
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceBean {

    @XmlElement(name = "name")
    private String serviceName;

    @XmlElement(name = "instance")
    private List<InstanceBean> instanceBeans;

}

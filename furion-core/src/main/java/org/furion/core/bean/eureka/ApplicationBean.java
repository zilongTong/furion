package org.furion.core.bean.eureka;


import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-18
 */
@XmlRootElement(name = "applications")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@ToString
public class ApplicationBean {

    @XmlElement(name = "application")
    private List<ServiceBean> serviceBeans;

}

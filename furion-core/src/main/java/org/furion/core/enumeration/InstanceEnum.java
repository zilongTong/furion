/*
package org.furion.furion.core.enumeration;

import org.furion.furion.core.bean.eureka.InstanceInfo;

*/
/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-24
 *//*

public class InstanceEnum {
    UP,
    DOWN,
    STARTING,
    OUT_OF_SERVICE,
    UNKNOWN;

    String status;

    private InstanceEnum(String status) {
        this.status = status;
    }

    public InstanceEnum toEnum(String s) {
        if (s != null) {
            try {
                return valueOf(s.toUpperCase());
            } catch (IllegalArgumentException var2) {
                InstanceInfo.logger.debug("illegal argument supplied to InstanceStatus.valueOf: {}, defaulting to {}", s, UNKNOWN);
            }
        }

        return UNKNOWN;
    }

}
*/

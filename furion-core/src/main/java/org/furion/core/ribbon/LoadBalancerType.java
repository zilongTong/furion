
package org.furion.core.ribbon;


public enum LoadBalancerType {
    ROUND_ROBIN,
    RANDOM,
    SPI;

    public static LoadBalancerType parse(String name) {
        for (LoadBalancerType s : values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    public static LoadBalancerType getDefault() {
        return RANDOM;
    }
}

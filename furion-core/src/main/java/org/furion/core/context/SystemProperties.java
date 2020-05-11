package org.furion.core.context;

import lombok.Data;
import org.furion.core.context.properties.BasePropertiesContainer;

@Data
public class SystemProperties extends BasePropertiesContainer {
    int port;
    String filterPath;
}

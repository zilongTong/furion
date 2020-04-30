package propertiesTest;

import lombok.Data;
import org.furion.core.context.properties.BasePropertiesContainer;

@Data
public class MyProperties extends BasePropertiesContainer {

    private String key1;
    private String key2;

}

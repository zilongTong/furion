package propertiesTest;

import lombok.Data;
import org.furion.core.context.properties.BasePropertiesContainer;

import java.util.List;

@Data
public class MyProperties extends BasePropertiesContainer {

    private String key1;
    private String key2;
    private boolean key3;
    private int key4;
    private double key5;
    private List<String> list;

}

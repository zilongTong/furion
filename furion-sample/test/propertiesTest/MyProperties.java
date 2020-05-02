package propertiesTest;

import lombok.Data;
import org.furion.core.context.properties.BasePropertiesContainer;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class MyProperties extends BasePropertiesContainer {

    private String key1;
    private String key2;
    private boolean key3;
    private int key4;
    private double key5;
    private List<String> list;
    private List<String> list2;
    private Set<String> set;
    private Map<String, InternalObject> map;


    public static class InternalObject {
        private String key1;
        private String key2;
        private boolean key3;
        private int key4;
        private double key5;
    }

}

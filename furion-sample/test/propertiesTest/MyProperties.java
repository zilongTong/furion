package propertiesTest;

import org.furion.core.annotation.PropertiesAutoRefresh;
import org.furion.core.context.properties.BasePropertiesContainer;

import java.util.List;
import java.util.Map;
import java.util.Set;

@PropertiesAutoRefresh
public class MyProperties<T> extends BasePropertiesContainer {

    private T t;
    private String key1;
    private String key2;
    private boolean key3;
    private int key4;
    private double key5;
    private List<String> list;
    private List<String> list2;
    private Set<String> set;
    private Map<String, InternalObject> map;


    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public boolean isKey3() {
        return key3;
    }

    public void setKey3(boolean key3) {
        this.key3 = key3;
    }

    public int getKey4() {
        return key4;
    }

    public void setKey4(int key4) {
        this.key4 = key4;
    }

    public double getKey5() {
        return key5;
    }

    public void setKey5(double key5) {
        this.key5 = key5;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getList2() {
        return list2;
    }

    public void setList2(List<String> list2) {
        this.list2 = list2;
    }

    public Set<String> getSet() {
        return set;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }

    public Map<String, InternalObject> getMap() {
        return map;
    }

    public void setMap(Map<String, InternalObject> map) {
        this.map = map;
    }

    public static class InternalObject {
        private String key1;
        private String key2;
        private boolean key3;
        private int key4;
        private double key5;


        public String getKey1() {
            return key1;
        }

        public void setKey1(String key1) {
            this.key1 = key1;
        }

        public String getKey2() {
            return key2;
        }

        public void setKey2(String key2) {
            this.key2 = key2;
        }

        public boolean isKey3() {
            return key3;
        }

        public void setKey3(boolean key3) {
            this.key3 = key3;
        }

        public int getKey4() {
            return key4;
        }

        public void setKey4(int key4) {
            this.key4 = key4;
        }

        public double getKey5() {
            return key5;
        }

        public void setKey5(double key5) {
            this.key5 = key5;
        }
    }

}

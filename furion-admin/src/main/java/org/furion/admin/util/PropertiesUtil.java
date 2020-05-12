package org.furion.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesUtil {
    static Properties properties = new Properties();

    static {
        try {
            File file = new File(PropertiesUtil.class.getResource("/config").getPath());
            if(file.exists() && file.isDirectory()){
                for(File f:file.listFiles()){
                    properties.load(new FileInputStream(f));
                }
            }
        }catch (Exception e){
            System.out.println("file not found"+e);
        }
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }

}

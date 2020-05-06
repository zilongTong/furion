package org.furion.core.filter;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;


public class LoadClassTest {


    static String filePath = "/Users/zilong/Downloads";

    public static Object newInstance(LClassLoader classLoader) {

        try {

            filePath = filePath + "/LeoRouteFilter.java";
            File f = new File(filePath);
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager manage = compiler.getStandardFileManager(null, null, null);
            Iterable iterable = manage.getJavaFileObjects(f);

            JavaCompiler.CompilationTask task = compiler.getTask(null, manage, null, null, null, iterable);
            task.call();
            manage.close();
            Class proxyClass = classLoader.findClass("LeoRouteFilter");
            FurionFilter furionFilter = (FurionFilter) proxyClass.newInstance();
            System.out.println(furionFilter.filterType());
            return proxyClass;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        File f = new File(filePath);
        LClassLoader classLoader = new LClassLoader(f);
        LClassLoader classLoader1 = new LClassLoader(f);
        newInstance(classLoader);
        newInstance(classLoader1);
    }

}

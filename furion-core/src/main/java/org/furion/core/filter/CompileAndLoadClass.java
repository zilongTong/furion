package org.furion.core.filter;

import com.google.common.collect.Sets;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;


public class CompileAndLoadClass {
    private static String filePath = "";

    public static Class newInstance(File file, LClassLoader classLoader) {

        try {


            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager manage = compiler.getStandardFileManager(null, null, null);
            Iterable iterable = manage.getJavaFileObjects(file);

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
        LClassLoader classLoader = new LClassLoader(Sets.newHashSet(f));
        newInstance(f, classLoader);
    }

}

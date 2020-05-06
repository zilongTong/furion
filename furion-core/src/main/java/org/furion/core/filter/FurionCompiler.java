package org.furion.core.filter;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FurionCompiler {

    private final String filePath;


    public FurionCompiler(String filePath) {
        this.filePath = filePath;
    }

    public void dynamicFilterCompile() {
        try {

            //.java
            File file = new File(filePath);

            if (file.isDirectory()) {
                File[] fs = file.listFiles();
                Arrays.stream(fs).forEach(f -> {
                    File temp = new File(filePath + f.getName());
                    //.java文件编译成.class文件
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                    StandardJavaFileManager manage = compiler.getStandardFileManager(null, null, null);
                    Iterable iterable = manage.getJavaFileObjects(temp);
                    JavaCompiler.CompilationTask task = compiler.getTask(null, manage, null, null, null, iterable);
                    task.call();

                    //.class
                    try {
                        manage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    try {
//                        Class dynamicFilter = classLoader.findClass(f.getName());
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        Class dynamicFilter = classLoader.findClass(f.getName());
//
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

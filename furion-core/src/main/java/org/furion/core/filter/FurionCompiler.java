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

    public void dynamicFilterCompile(FilterClassLoader classLoader) {
        try {
            File file = new File(filePath);
            if (file.isDirectory()) {
                File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
                Arrays.stream(fs).forEach(f -> {
                    f.getName();
                    File temp = new File(filePath + f.getName());
                    //3、把生成的.java文件编译成.class文件
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                    StandardJavaFileManager manage = compiler.getStandardFileManager(null, null, null);
                    Iterable iterable = manage.getJavaFileObjects(temp);
                    JavaCompiler.CompilationTask task = compiler.getTask(null, manage, null, null, null, iterable);
                    task.call();
                    try {
                        manage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //4、编译生成的.class文件加载到JVM中来
                    try {
                        Class dynamicFilter = classLoader.findClass(f.getName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

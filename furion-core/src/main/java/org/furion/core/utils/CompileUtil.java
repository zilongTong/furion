package org.furion.core.utils;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author wplin
 * 2020年05月05日20:28:53
 */
public class CompileUtil {
    private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    /**
     * 一个java源文件，可能编译出 多个 Class。
     * 输出到指定目录
     */
    public static Set<File> compileJavaFileToClassFiles(File file, String outDirPath) {
        String classFileDir = outDirPath + File.separator + file.getName().replace(".java", "" + "classFile" + File.separator);
        Set<File> classFiles = Sets.newHashSet();
        try {
            File outDir = new File(classFileDir);
            if (!outDir.exists()) {
                boolean newFile = outDir.mkdir();
                if (!newFile) {
                    throw new RuntimeException("创建目录失败：" + classFileDir);
                }
            }
            if (!outDir.isDirectory()) {
                throw new RuntimeException("编译输出目录错误：不是目录 " + classFileDir);
            }
            if (!outDir.canWrite() || !outDir.canRead()) {
                throw new RuntimeException("编译输出目录无权限读写：" + classFileDir);
            }
            //清空目录
            FileUtils.cleanDirectory(outDir);

            StandardJavaFileManager manage = compiler.getStandardFileManager(null, null, null);
            Iterable iterable = manage.getJavaFileObjects(file);
            List<String> options = new ArrayList<>();
            options.add("-d");
            options.add(classFileDir);
            JavaCompiler.CompilationTask task = compiler.getTask(null, manage, null, options, null, iterable);
            task.call();
            manage.close();
            //扫描所有已生成的 class 文件
            Set<File> files1 = FileUtil.listFileFromDir(classFileDir, new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".class");
                }
            });
            classFiles.addAll(files1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classFiles;
    }


}

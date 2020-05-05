package org.furion.core.utils;

import com.google.common.collect.Sets;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Set;

public class FileUtil {
    /**
     * 递归扫描路径，返回所有文件
     */
    public static Set<File> listFileFromDir(String dirPath, FilenameFilter filenameFilter) {
        File file = new File(dirPath);
        Set<File> set = Sets.newHashSet();
        doFilter(file, filenameFilter, set);
        return set;
    }

    private static void doFilter(File file, FilenameFilter filenameFilter, Set<File> set) {
        if (file.isFile()) {
            return;
        }
        if (file.isDirectory()) {
            //过滤目标文件
            File[] files = file.listFiles(filenameFilter);
            if (files != null) {
                set.addAll(Sets.newHashSet(files));
            }
            //递归扫描 目录
            File[] all = file.listFiles();
            for (File item : all) {
                if (item.isDirectory()) {
                    doFilter(item, filenameFilter, set);
                }
            }
        }
    }

}

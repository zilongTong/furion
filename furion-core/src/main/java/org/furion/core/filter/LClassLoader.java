package org.furion.core.filter;

import com.google.common.collect.Sets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

/**
 * Filter类加载器。为保证旧Class能卸载，此LClassLoader只能使用一次。
 */
public class LClassLoader extends ClassLoader {
    private boolean hasUsed = false;
    private Set<File> classPathFile;
    private volatile File currentClassFile;

    public LClassLoader(Set<File> classPathFile) {
        this.classPathFile = classPathFile;
    }

    @Override
    protected Class<?> findClass(String name) {

        if (currentClassFile.exists()) {
            String className = currentClassFile.getName();
            FileInputStream in = null;
            ByteArrayOutputStream out = null;

            try {
                in = new FileInputStream(currentClassFile);
                out = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int len;
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                return defineClass(className, out.toByteArray(), 0, out.size());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    public Set<Class<?>> loadClasses() {
        if (hasUsed) {
            throw new RuntimeException("类加载器必须重新实例化");
        }
        Set<Class<?>> classes = Sets.newHashSet();
        if (classPathFile != null && !classPathFile.isEmpty()) {
            int index = 0;
            for (File item : classPathFile) {
                this.currentClassFile = item;
                //+ System.currentTimeMillis() % 100 + "$" + index
                classes.add(findClass(item.getName()));
                index++;
            }
        }
        hasUsed = true;
        return classes;
    }
}

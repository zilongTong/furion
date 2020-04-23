package org.furion.core.utils;

import org.checkerframework.checker.units.qual.C;

import java.io.*;

public class ClassLoaderUtil extends ClassLoader {

    private static ClassLoaderUtil classLoaderUtil = new ClassLoaderUtil();

    /**
     * 文件、网络资源 由外部准备好
     */
    public static Class<?> loadClass(ByteArrayInputStream inputStream) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = inputStream) {
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            return classLoaderUtil.defineClass(null, out.toByteArray(), 0, out.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

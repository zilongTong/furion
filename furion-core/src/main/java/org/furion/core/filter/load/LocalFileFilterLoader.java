package org.furion.core.filter.load;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.furion.core.bean.ClassResourceContainer;
import org.furion.core.context.FurionGatewayContext;

import java.io.*;
import java.util.List;

/**
 * 加载指定本地路径下 Filter Class文件。
 * 来源:用户主动上传、网络传输的class文件 本地缓存
 * 相同文件只加载一次
 */
public class LocalFileFilterLoader extends BaseFilterLoader {

    @Override
    List<ClassResourceContainer> getResource(FurionGatewayContext context) {
        List<ClassResourceContainer> list = Lists.newArrayList();
        String filterFilePath = fixPath();
        if (StringUtils.isBlank(filterFilePath)) {
            return list;
        }
        File file = new File(filterFilePath);
        if (!file.exists()) {
            return list;
        }
        return listFileInputStream(file);
    }

    /**
     * 返回 class file 文件 inputStream list
     */
    private List<ClassResourceContainer> listFileInputStream(File file) {
        return null;
    }


    /**
     * 对路径做检查修复
     * 返回 绝对路径 /path/to/file/
     */
    private String fixPath() {
        //filter class文件地址
        return propertiesManager.getSinglePropertyValue("filter.filePath", String.class);
    }

}
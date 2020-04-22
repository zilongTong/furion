package org.furion.core.bean;

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * @author wplin
 */
@Data
public class ClassResourceContainer {

    /**
     * class 资源的唯一表示，如：绝对路径地址，url,等
     */
    private String ClassResourceId;
    /**
     * 资源最后更新时间，装载已最后修改版本为准，之前时间的会被覆盖
     */
    private Date lastUpdateTime;
    /**
     * 可以重复读取流
     */
    private ByteArrayInputStream byteArrayInputStream;
}

package org.furion.core.bean;

import org.furion.core.annotation.ApiField;

import java.io.Serializable;
import java.util.Date;

public class BaseModel implements Serializable {

    private static final long serialVersionUID = 3253510988399344793L;
    /**
     *
     */
    @ApiField("createTime")
    private Date createTime;

    @ApiField("createUserId")
    private Long createUserId;

    @ApiField("updateTime")
    private Date updateTime;

    @ApiField("updateUserId")
    private Long updateUserId;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

}

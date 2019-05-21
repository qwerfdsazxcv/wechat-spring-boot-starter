package com.mili.wechat.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BaseDo implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键
     */
    @Id
    protected String id;

    /**
     * 备注
     */
    protected String remark;

    /**
     * 扩展信息
     */
    protected String extraInfo;

    /**
     * 创建人
     */
    protected String creator;

    /**
     * 创建时间
     */
    @Field(value = "gmt_created")
    protected Date gmtCreated;

    /**
     * 修改人
     */
    protected String modifier;

    /**
     * 最后修改时间
     */
    @Field(value = "gmt_modified")
    protected Date gmtModified;

    /**
     * 是否删除
     */
    @Field(value = "is_deleted")
    protected String isDeleted;
}

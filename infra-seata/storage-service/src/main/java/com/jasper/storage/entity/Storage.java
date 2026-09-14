package com.jasper.storage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("storage_tbl")
public class Storage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String commodityCode;
    private Integer count;
}

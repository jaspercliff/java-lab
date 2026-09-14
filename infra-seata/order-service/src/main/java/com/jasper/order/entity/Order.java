package com.jasper.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("order_tbl")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userId;
    // 商品id
    private String commodityCode;
    private Integer count;
    private Integer money;
}

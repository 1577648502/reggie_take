package com.lfg.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐
 */
@ApiModel(description = "套餐")
@Data
public class Setmeal implements Serializable {

    @ApiModelProperty("")
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("")
    private Long id;


    //分类id
    @ApiModelProperty("分类id")
    private Long categoryId;


    //套餐名称
    @ApiModelProperty("套餐名称")
    private String name;


    //套餐价格
    @ApiModelProperty("套餐价格")
    private BigDecimal price;


    //状态 0:停用 1:启用
    @ApiModelProperty("状态 0:停用 1:启用")
    private Integer status;


    //编码
    @ApiModelProperty("编码")
    private String code;


    //描述信息
    @ApiModelProperty("描述信息")
    private String description;


    //图片
    @ApiModelProperty("图片")
    private String image;


    @ApiModelProperty("")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @ApiModelProperty("")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @ApiModelProperty("")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @ApiModelProperty("")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


    //是否删除
    @ApiModelProperty("是否删除")
    private Integer isDeleted;
}
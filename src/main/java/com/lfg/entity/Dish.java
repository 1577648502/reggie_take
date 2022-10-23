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
 菜品
 */
@ApiModel(description = "菜品")
@Data
public class Dish implements Serializable {

    @ApiModelProperty("")
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("")
    private Long id;


    //菜品名称
    @ApiModelProperty("菜品名称")
    private String name;


    //菜品分类id
    @ApiModelProperty("菜品分类id")
    private Long categoryId;


    //菜品价格
    @ApiModelProperty("菜品价格")
    private BigDecimal price;


    //商品码
    @ApiModelProperty("商品码")
    private String code;


    //图片
    @ApiModelProperty("图片")
    private String image;


    //描述信息
    @ApiModelProperty("描述信息")
    private String description;


    //0 停售 1 起售
    @ApiModelProperty("0 停售 1 起售")
    private Integer status;


    //顺序
    @ApiModelProperty("顺序")
    private Integer sort;


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

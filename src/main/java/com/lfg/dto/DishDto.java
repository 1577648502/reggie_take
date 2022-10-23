package com.lfg.dto;

import com.lfg.entity.Dish;
import com.lfg.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@ApiModel
@Data
public class DishDto extends Dish {
    @ApiModelProperty("DishFlavor")
    private List<DishFlavor> flavors = new ArrayList<>();

    @ApiModelProperty("categoryName")
    private String categoryName;

    @ApiModelProperty("copies")
    private Integer copies;
}

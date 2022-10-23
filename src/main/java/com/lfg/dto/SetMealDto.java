package com.lfg.dto;

import com.lfg.entity.Setmeal;
import com.lfg.entity.SetmealDish;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class SetMealDto extends Setmeal {
    @ApiModelProperty("SetmealDish")
    private List<SetmealDish> setMealDishes;

    @ApiModelProperty("categoryName")
    private String categoryName;
}

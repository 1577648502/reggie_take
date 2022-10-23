package com.lfg.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lfg.dto.DishDto;
import com.lfg.dto.SetMealDto;
import com.lfg.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐与菜品的关联关系
     * @param setMealDto
     */
    public void saveWithDish(SetMealDto setMealDto);

    public void removeWithDish(List<Long> ids);

    //根据id查询套餐信息和对应的口味信息
    public SetMealDto getByIdWithDish(Long id);

    //修改套餐信息
    public void updateWithDishes(SetMealDto setMealDto);

    void updateStatusById(Long id);

//    void delete(Long id);
}

package com.lfg.service.impl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfg.common.CustomException;
import com.lfg.entity.Category;
import com.lfg.entity.Dish;
import com.lfg.entity.Employee;
import com.lfg.entity.Setmeal;
import com.lfg.mapper.CategoryMapper;
import com.lfg.mapper.EmployeeMapper;
import com.lfg.service.impl.CategoryService;
import com.lfg.service.impl.DishService;
import com.lfg.service.impl.EmployeeService;
import com.lfg.service.impl.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，如果关联了，抛出异常
        if(count>0){
            //已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setMealService.count(setmealLambdaQueryWrapper);

        if (count1>0){
            //已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}

package com.lfg.service.impl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfg.common.CustomException;
import com.lfg.dto.DishDto;
import com.lfg.dto.SetMealDto;
import com.lfg.entity.Dish;
import com.lfg.entity.DishFlavor;
import com.lfg.entity.Setmeal;
import com.lfg.entity.SetmealDish;
import com.lfg.mapper.SetMealDishMapper;
import com.lfg.mapper.SetMealMapper;
import com.lfg.service.impl.SetMealDishService;
import com.lfg.service.impl.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private SetMealMapper setmealMapper;

    /**
     * 新增套餐，同时需要保存套餐与菜品的关联关系
     *
     * @param setMealDto
     */
    @Transactional
    public void saveWithDish(SetMealDto setMealDto) {
        //保存套餐的基本信息，操作setMeal，执行insert操作
        this.save(setMealDto);
        List<SetmealDish> setMealDishes = setMealDto.getSetMealDishes();
        setMealDishes.stream().map((item -> {
            item.setSetmealId(setMealDto.getId());
            return item;
        })).collect(Collectors.toList());
        //保存套餐和菜品的关联信息，操作setMeal_dish,执行insert操作

        setMealDishService.saveBatch(setMealDishes);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        //查询状态，确认是否可删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        //如果不能删除，抛出一个异常
        if(count>0){
            throw  new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据
       LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
       lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

       setMealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    public SetMealDto getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        SetMealDto setmealDto = new SetMealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishes = setMealDishMapper.selectList(dishLambdaQueryWrapper);
        setmealDto.setSetMealDishes(setmealDishes);
        //setmealMapper.selectById(id);

        return setmealDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value ="SetmealCache",allEntries = true)
    public void updateWithDishes(SetMealDto setMealDto) {
        //更新setmeal表信息
        setmealMapper.updateById(setMealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setMealDto.getId());
        setMealDishMapper.delete(queryWrapper);
        Long id = setMealDto.getId();
        List<SetmealDish> setmealDishes = setMealDto.getSetMealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
            setMealDishMapper.insert(setmealDish);
        }
//        String image = setMealDto.getImage();
//        redisUtils.save2Db(image);

    }

    @Override
    public void updateStatusById(Long id) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getId,id);
        Setmeal setmeal = setmealMapper.selectById(id);
        if (setmeal.getStatus() == 0 ){
            setmeal.setStatus(1);
        }else{
            setmeal.setStatus(0);
        }
        setmealMapper.updateById(setmeal);
    }


//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    @CacheEvict(value ="SetmealCache",allEntries = true)
//    public void delete(Long id) {
//        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Setmeal::getId,id);
//        Setmeal setmeal = setmealMapper.selectById(id);
//        if (setmeal.getStatus() != 0){
//            throw new CustomException("当前套餐为售卖状态，不能删除！");
//        }
//        setmealMapper.deleteById(id);
//        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
////        redisUtils.removePicFromRedis(setmeal.getImage());
//        setMealDishMapper.delete(setmealDishLambdaQueryWrapper);
//
//    }


}

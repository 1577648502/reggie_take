package com.lfg.service.impl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfg.common.CustomException;
import com.lfg.dto.DishDto;
import com.lfg.entity.Dish;
import com.lfg.entity.DishFlavor;
import com.lfg.entity.SetmealDish;
import com.lfg.mapper.DishFlavorMapper;
import com.lfg.mapper.DishMapper;
import com.lfg.mapper.SetMealMapper;
import com.lfg.service.impl.DishFlavorService;
import com.lfg.service.impl.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品，同时保存对应到口味数据
     * @param dishDto
     */
    @Override
    //事务控制注解
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本数据到菜品表dish
        this.save(dishDto);
        //菜品id
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据   dish_flavor 表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据   dish_flavor 表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @CacheEvict(value = "DishCache",allEntries = true)
    public void updateStatusById(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getId,id);
        Dish dish = dishMapper.selectById(id);
        if (dish.getStatus() == 0){
            dish.setStatus(1);
        }else {
            dish.setStatus(0);
        }
        dishMapper.updateById(dish);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DishCache",allEntries = true)
    public void delete(Long aLong) {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getId,aLong);
        Dish dish = dishMapper.selectById(aLong);
        if (dish.getStatus() != 0){
            throw new CustomException("当前菜品为售卖状态，不能删除！");
        }
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(DishFlavor::getDishId,aLong);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(queryWrapper1);
        if (dishFlavors != null){
            dishFlavorMapper.delete(queryWrapper1);
        }
        LambdaQueryWrapper<SetmealDish> queryWrapper2 = new LambdaQueryWrapper<>();
//        queryWrapper2.eq(SetmealDish::getDishId,aLong);
//        Long selectCount = setmealDishMapper.selectCount(queryWrapper2);
//        if (selectCount > 0 ){
//            throw new CustomException("当前菜品存在关联套餐，不能删除！请先在套餐中删除菜品！");
//        }
//        redisUtils.removePicFromRedis(dish.getImage());
        dishMapper.delete(queryWrapper);


    }
}

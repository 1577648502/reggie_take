package com.lfg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lfg.common.BaseContext;
import com.lfg.common.R;
import com.lfg.entity.ShoppingCart;
import com.lfg.service.impl.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Api(value = "shoppingCart", tags = {"购物车"})
@RestController
@RequestMapping("shoppingCart")

public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "ShoppingCart", name = "shoppingCart", value = "", required = true)
    })
    @ApiOperation(value = "添加购物车", notes = "", httpMethod = "POST")
    @PostMapping("add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //设置用户id，指定当前是哪个用户的购物车数据

        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或者套餐是否已存在购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //添加到是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);


        } else {
            //添加到是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne != null) {
            //如果已存在数量+1
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //不存在，添加到购物车，数量默认是1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "ShoppingCart", name = "shoppingCart", value = "", required = true)
    })
    @ApiOperation(value = "减少购物车", notes = "", httpMethod = "POST")
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
//        User userInfo = UserThreadLocal.get();
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCart.getDishId() != null,ShoppingCart::getDishId,shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId() != null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one.getNumber() == 1){
            shoppingCartService.removeById(one);
        }
        Integer number = one.getNumber();
        one.setNumber(number-1);
        shoppingCartService.updateById(one);
        return R.success(one);
    }

    /**
     * 获取购物车列表
     *
     * @return
     */
    @ApiOperation(value = "获取购物车列表", notes = "获取购物车列表", httpMethod = "GET")
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }


    /**
     * 清空购物车
     *
     * @return
     */
    @ApiOperation(value = "清空购物车", notes = "清空购物车", httpMethod = "DELETE")
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);

        return R.success("清空成功");
    }
}

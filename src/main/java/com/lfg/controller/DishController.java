package com.lfg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfg.common.R;
import com.lfg.dto.DishDto;
import com.lfg.entity.Category;
import com.lfg.entity.Dish;
import com.lfg.entity.DishFlavor;
import com.lfg.service.impl.CategoryService;
import com.lfg.service.impl.DishFlavorService;
import com.lfg.service.impl.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */

@Api(value = "dish", tags = {"菜品管理"})
@RestController
@RequestMapping("dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "DishDto", name = "dishDto", value = "", required = true)
    })
    @ApiOperation(value = "新增菜品", notes = "新增菜品", httpMethod = "POST")
    @PostMapping
    public R<String> list(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "name", value = "", required = true)
    })
    @ApiOperation(value = "菜品信息分页", notes = "菜品信息分页", httpMethod = "GET")
    @GetMapping("page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造跟页构造对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造起
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 按id查询菜品
     * @param id
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "long", name = "id", value = "", required = true)
    })
    @ApiOperation(value = "按id查询菜品", notes = "按id查询菜品", httpMethod = "GET")
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "DishDto", name = "dishDto", value = "", required = true)
    })
    @ApiOperation(value = "修改菜品", notes = "修改菜品", httpMethod = "PUT")
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

//    /**
//     * 根据条件查询菜品数据
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //添加条件，查询状态为1（起售状态）的
//        queryWrapper.eq(Dish::getStatus,1);
//
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Dish", name = "dish", value = "", required = true)
    })
    @ApiOperation(value = "根据条件查询菜品数据", notes = "根据条件查询菜品数据", httpMethod = "GET")
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();

            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }

    /**
     * 开启关闭菜品
     * @param ids
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Long[]", name = "ids", value = "", required = true)
    })
    @ApiOperation(value = "开启关闭菜品", notes = "开启关闭菜品", httpMethod = "POST")
    @PostMapping(value = {"/status/0", "/status/1"})
    public R<String> status(@RequestParam("ids") Long[] ids) {
        for (Long aLong : ids) {
            dishService.updateStatusById(aLong);
        }
        return R.success("菜品状态修改成功");

    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Long[]", name = "ids", value = "", required = true)
    })
    @ApiOperation(value = "删除菜品", notes = "删除菜品", httpMethod = "DELETE")
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long[] ids){
        for (Long id : ids) {
            dishService.delete(id);
        }
        return R.success("删除成功");
    }
}


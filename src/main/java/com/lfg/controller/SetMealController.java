package com.lfg.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfg.common.R;
import com.lfg.dto.SetMealDto;
import com.lfg.entity.Category;
import com.lfg.entity.Setmeal;
import com.lfg.service.impl.CategoryService;
import com.lfg.service.impl.SetMealDishService;
import com.lfg.service.impl.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Lang;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Api(value = "/setmeal", tags = {"套餐管理"})
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐
     *
     * @param setMealDto
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "SetMealDto", name = "setMealDto", value = "", required = true)
    })
    @ApiOperation(value = "新增套餐", notes = "新增套餐", httpMethod = "POST")
    @PostMapping
    public R<String> save(@RequestBody SetMealDto setMealDto) {
        setMealService.saveWithDish(setMealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
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
    @ApiOperation(value = "获取套餐列表-分页查询", notes = "套餐分页查询", httpMethod = "GET")
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetMealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件，根据更新事件降序排序

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        Page<Setmeal> setMealPage = setMealService.page(pageInfo, queryWrapper);


        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetMealDto> list = records.stream().map((item) -> {
            SetMealDto setMealDto = new SetMealDto();
            BeanUtils.copyProperties(item, setMealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setMealDto.setCategoryName(categoryName);
            }
            return setMealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "List<Long>", name = "ids", value = "", required = true)
    })
    @ApiOperation(value = "删除套餐", notes = "删除套餐", httpMethod = "DELETE")
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setMealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 按id查询套餐信息
     * @param id
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "long", name = "id", value = "", required = true)
    })
    @ApiOperation(value = "按id查询套餐信息", notes = "按id查询套餐信息", httpMethod = "GET")
    @GetMapping("/{id}")
    public R<SetMealDto> get(@PathVariable Long id){
        SetMealDto SetMealDto = setMealService.getByIdWithDish(id);
        if (SetMealDto != null){
            return R.success(SetMealDto);
        }
        return R.error("没有查询到对应套餐信息");
    }

    /**
     * 修改套餐信息
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "SetMealDto", name = "setMealDto", value = "", required = true)
    })
    @ApiOperation(value = "修改套餐信息", notes = "修改套餐信息", httpMethod = "PUT")
    @PutMapping
    public R<String> update(@RequestBody SetMealDto setMealDto){
        setMealService.updateWithDishes(setMealDto);
        return R.success("套餐信息修改成功");
    }

    /**
     * 修改套餐状态
     * @param ids
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Long[]", name = "ids", value = "", required = true)
    })
    @ApiOperation(value = "修改套餐状态 是否起售", notes = "修改套餐状态", httpMethod = "POST")
    @PostMapping(value = {"/status/0","/status/1"})
    public R<String> status(@RequestParam("ids") Long[] ids){
        for (Long aLong : ids) {
            setMealService.updateStatusById(aLong);
        }
        return R.success("套餐状态修改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Setmeal", name = "setmeal", value = "", required = true)
    })
    @ApiOperation(value = "按条件查询套餐数据", notes = "根据条件查询套餐数据", httpMethod = "GET")
    @GetMapping("list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setMealService.list(queryWrapper);
        return R.success(list);
    }

//    /**
//     * 删除套餐
//     * @param id
//     * @return
//     */
//    @DeleteMapping
//    public R<String> delete(Long[] id){
//        for (Long aLong : id) {
//            setMealService.delete(aLong);
//        }
//        return R.success("删除套餐成功！");
//    }
}

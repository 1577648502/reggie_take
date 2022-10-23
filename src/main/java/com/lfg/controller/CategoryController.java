package com.lfg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfg.common.R;
import com.lfg.entity.Category;
import com.lfg.service.impl.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */

@Api(value = "category", tags = {"分类管理"})
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    /**
     * 保存分类
     * @param category
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "Category", name = "category", value = "", required = true)
    })
    @ApiOperation(value = "保存分类", notes = "保存分类", httpMethod = "POST")
    @PostMapping
    public R<Category> save(@RequestBody Category category) {

        categoryService.save(category);

        return R.success(category);
    }

    /**
     * 分页查询分类
     * @param page
     * @param pageSize
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "", required = true)
    })
    @ApiOperation(value = "分页查询分类", notes = "分页查询分类", httpMethod = "GET")
    @GetMapping("page")
    public R<Page> page(int page, int pageSize) {
        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "long", name = "ids", value = "", required = true)
    })
    @ApiOperation(value = "根据id删除分类", notes = "根据id删除分类", httpMethod = "DELETE")
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
//        categoryService.removeById(ids);
        return R.success("删除成功");
    }

    /**
     * 根据id更新信息
     * @param category
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "Category", name = "category", value = "", required = true)
    })
    @ApiOperation(value = "根据id更新信息", notes = "根据id更新信息", httpMethod = "PUT")
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Category", name = "category", value = "", required = true)
    })
    @ApiOperation(value = "根据条件查询分类数据", notes = "根据条件查询分类数据", httpMethod = "GET")
    @GetMapping("list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(category.getType()!= null,Category::getType,category.getType());

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }


}

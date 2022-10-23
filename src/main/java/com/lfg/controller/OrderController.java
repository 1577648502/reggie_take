package com.lfg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfg.common.R;
import com.lfg.entity.OrderDetail;
import com.lfg.entity.Orders;
import com.lfg.service.impl.OrderDetailService;
import com.lfg.service.impl.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Api("/order")
@RestController
@RequestMapping("/order")

public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "Orders", name = "orders", value = "", required = true)
    })
    @ApiOperation(value = "", notes = "", httpMethod = "POST")
    @PostMapping("submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("成功");
    }
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "number", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "beginTime", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "endTime", value = "", required = true)
    })
    @ApiOperation(value = "", notes = "", httpMethod = "GET")
    @GetMapping("page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){

        Page pageInfo = new Page<>(page,pageSize);
        return orderService.pageQuery(pageInfo,number,beginTime,endTime);
    }
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "Orders", name = "orders", value = "", required = true)
    })
    @ApiOperation(value = "", notes = "", httpMethod = "PUT")
    @PutMapping
    public R<String> put(@RequestBody Orders orders){
        orderService.updateById(orders);
        return R.success("状态更新成功");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "", required = true)
    })
    @ApiOperation(value = "", notes = "", httpMethod = "GET")
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page pageInfo = new Page<>(page,pageSize);
        return orderService.userPage(pageInfo);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "long", name = "id", value = "", required = true)
    })
    @ApiOperation(value = "", notes = "", httpMethod = "DELETE")
    @DeleteMapping
    @Transactional(rollbackFor = Exception.class)
    public R<String> deleteOrder(Long id){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        orderDetailService.remove(queryWrapper);
        orderService.removeById(id);
        return R.success("删除成功！");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "Orders", name = "orders", value = "", required = true)
    })
    @ApiOperation(value = "", notes = "", httpMethod = "POST")
    @PostMapping("/again")
    public R<String> addOrderAgain(@RequestBody Orders orders){
        if (orders.getId() != null){
            return R.success("成功！");
        }
        return R.error("失败!");
    }

    @ApiOperation(value = "", notes = "", httpMethod = "GET")
    @GetMapping("/getToDayOrder")
    public R<Long> getToDayOrder(){
        return orderService.countToDayOrder();
    }

    @ApiOperation(value = "", notes = "", httpMethod = "GET")
    @GetMapping("/getYesDayOrder")
    public R<Long> getYesDayOrder(){
        return orderService.countYesDayOrder();
    }

    @ApiOperation(value = "", notes = "", httpMethod = "GET")
    @GetMapping("/getOneWeekLiuShui")
    public R<Map> getOneWeekLiuShui(){
        return orderService.OneWeekLiuShui();
    }

    @ApiOperation(value = "", notes = "", httpMethod = "GET")
    @GetMapping("/getOneWeekOrder")
    public R<Map> getOneWeekOrder(){
        return orderService.OneWeekOrder();
    }

    @ApiOperation(value = "", notes = "", httpMethod = "GET")
    @GetMapping("/getHotSeal")
    public R<Map> getHotSeal(){
        return orderService.hotSeal();
    }
}

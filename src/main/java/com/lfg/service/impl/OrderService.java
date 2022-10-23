package com.lfg.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lfg.common.R;
import com.lfg.dto.OrdersDto;
import com.lfg.entity.Orders;

import java.util.Map;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);


    R<Page> pageQuery(Page pageInfo, String number, String beginTime, String endTime);

    /**
     * 通过订单号修改订单状态
     * @param ordersDto
     */
    void updateStatusById(OrdersDto ordersDto);

    /**
     * 用户订单分页查询
     * @param pageInfo
     * @return
     */
    R<Page> userPage(Page pageInfo);

    /**
     * 查询今日订单量
     * @return
     */
    R<Long> countToDayOrder();

    /**
     * 查询昨日订单量
     * @return
     */
    R<Long> countYesDayOrder();

    /**
     * 查询近一周流水
     * @return
     */
    R<Map> OneWeekLiuShui();

    /**
     * 查询近一周订单数量
     * @return
     */
    R<Map> OneWeekOrder();

    /**
     * 查询热卖套餐
     * @return
     */
    R<Map> hotSeal();
}

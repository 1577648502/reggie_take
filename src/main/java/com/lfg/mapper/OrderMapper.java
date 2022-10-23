package com.lfg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lfg.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}

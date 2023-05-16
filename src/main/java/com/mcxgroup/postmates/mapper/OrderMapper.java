package com.mcxgroup.postmates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mcxgroup.postmates.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}

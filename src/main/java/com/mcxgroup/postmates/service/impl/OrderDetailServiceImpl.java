package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcxgroup.postmates.entity.OrderDetail;
import com.mcxgroup.postmates.mapper.OrderDetailMapper;
import com.mcxgroup.postmates.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @Description: 订单详情服务实现类
 * @author: MCXEN
 * @date: 2022/11/30
 * MCXEN
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}

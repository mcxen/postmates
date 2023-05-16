package com.mcxgroup.postmates.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mcxgroup.postmates.entity.Order;

public interface OrderService extends IService<Order> {
    void submit(Order order);
}

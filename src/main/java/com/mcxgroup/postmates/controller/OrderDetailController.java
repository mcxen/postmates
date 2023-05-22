package com.mcxgroup.postmates.controller;

import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.entity.OrderDetail;
import com.mcxgroup.postmates.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/{id}")
    public R<OrderDetail> get(@PathVariable Long id){
        OrderDetail byId = orderDetailService.getById(id);
        return R.success(byId);
    }
}

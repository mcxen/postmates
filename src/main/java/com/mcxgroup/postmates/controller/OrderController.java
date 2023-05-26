package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.dto.OrderDto;
import com.mcxgroup.postmates.entity.Order;
import com.mcxgroup.postmates.entity.OrderDetail;
import com.mcxgroup.postmates.service.OrderDetailService;
import com.mcxgroup.postmates.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/order")
@Slf4j
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Order order){
        log.info("正在执行order的submit{}",order);
        orderService.submit(order);
        return R.success("下单成功");
    }

    //分页展示订单详情（管理员端展示）
    @GetMapping("/page")
    public R<Page<Order>> page(int page,int pageSize,Long number,String beginTime,String endTime){
        log.info("正在执行order的分页展示订单详情（管理员）{}");
        Page<Order> orderPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(number!=null,Order::getNumber,number);
        wrapper.gt(StringUtils.isNotEmpty(beginTime),Order::getOrderTime,beginTime);
        wrapper.lt(StringUtils.isNotEmpty(endTime),Order::getOrderTime,endTime);
        wrapper.orderByDesc(Order::getOrderTime);
        orderService.page(orderPage,wrapper);
        return R.success(orderPage);
    }
    @PutMapping
    public R<Order> updateStatus(@RequestBody Order order){
        Integer status = order.getStatus();
//        if (status != null) {
//            order.setStatus(3);
//        }
        orderService.updateById(order);
        log.info("订单修改状态，{}",order.toString());
        return R.success(order);
    }

    @GetMapping("/userPage")
    public R<Page<OrderDto>> page(int page,int pageSize){
        // 使用dto的分页
        log.info("userPage的手机端的展示订单order详情");
        Page<Order> orderPage = new Page<>(page,pageSize);
        Page<OrderDto> orderDtoPage = new Page<>(page,pageSize);

        //构造查询wrapper
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getUserId, BaseContext.getCurrentId());
        orderWrapper.orderByDesc(Order::getOrderTime);
        // 注入orderPage
        orderService.page(orderPage,orderWrapper);
        //对OrderDto进行需要的属性赋值
        List<Order> records = orderPage.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> list = orderDetailService.list(wrapper);
            BeanUtils.copyProperties(item, orderDto);
            orderDto.setOrderDetails(list);
            return orderDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(orderPage,orderDtoPage,"records");
        orderDtoPage.setRecords(orderDtoList);
        return R.success(orderDtoPage);
    }

    @PostMapping("/again")
    public R<Order> again(@RequestBody Order order){
        Order newOrder = orderService.getById(order.getId());//上一单
        log.info(newOrder.toString());
        newOrder.setStatus(1);
        orderService.submit(newOrder);
        return R.success(newOrder);
    }
}

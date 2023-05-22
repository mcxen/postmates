package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.CustomException;
import com.mcxgroup.postmates.entity.*;
import com.mcxgroup.postmates.mapper.OrderMapper;
import com.mcxgroup.postmates.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImp extends ServiceImpl<OrderMapper,Order> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Override
    @Transactional
    public void submit(Order order) {
        //插入数据，完善订单明细表
        //获取userId
        log.info("正在执行orderService的submit{}",order);
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        // 一个用户可能下单了 多个菜品或套餐，故应该查询到的是一个 购物车类型的list
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);
        if (shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车为空，不可下单");
        }
        //current User
        User user = userService.getById(userId);
        //user's address'
        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());
        if (addressBook == null)
            throw new CustomException("您的地址有误，请重新下单！");
        long orderId = IdWorker.getId();//Create an Id for order
        //  购物车中 商品 的总金额 需要保证在多线程的情况下 也是能计算正确的，故需要使用原子类
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            // 设置用户名
            orderDetail.setName(user.getName());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            // 总金额 = 单价 * 份数
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        order.setId(orderId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setStatus(2);
        order.setAmount(new BigDecimal(amount.get()));//总金额，需要 遍历购物车，计算相关金额来得到
        order.setUserId(userId);
        order.setNumber(String.valueOf(orderId));
        order.setUserName(user.getName());
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(order);//  --> ordersService.save(orders);
        // 向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        // 清空购物车数据
        shoppingCartService.remove(wrapper);
    }
}

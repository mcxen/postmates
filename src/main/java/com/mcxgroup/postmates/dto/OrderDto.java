package com.mcxgroup.postmates.dto;

import com.mcxgroup.postmates.entity.Order;
import com.mcxgroup.postmates.entity.OrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Order {
    private String userName;
    private String phone;
    private String address;
    private String consignee;
    private List<OrderDetail> orderDetails;
}

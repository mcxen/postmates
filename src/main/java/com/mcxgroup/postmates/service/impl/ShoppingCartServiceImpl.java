package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcxgroup.postmates.entity.ShoppingCart;
import com.mcxgroup.postmates.mapper.ShoppingCartMapper;
import com.mcxgroup.postmates.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @Description: 购物车服务实现类
 * @author: MCXEN
 * @date: 2022/11/30
 * MCXEN
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper,ShoppingCart> implements ShoppingCartService {
}

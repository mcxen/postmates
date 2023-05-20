package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.entity.ShoppingCart;
import com.mcxgroup.postmates.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

//    @Autowired
//    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    public R<ShoppingCart> addToCart(@RequestBody ShoppingCart shoppingCart) {
        //增加购物车的物品
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        wrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        //在购物车中查询
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(wrapper);
        // 购物车存在该菜品，则仅增加该菜品的数量即可
        if (shoppingCartServiceOne!=null){
            shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber()+1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }else {
            shoppingCartService.save(shoppingCart);
        }
        return R.success(shoppingCart);
    }

    // 在购物车中删减订单
    @PostMapping("/sub")
    public R<String> subToCart(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车中的数据:{}" + shoppingCart.toString());

        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询当前菜品或套餐是否 在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 根据登录用户的 userId去ShoppingCart表中查询该用户的购物车数据
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        // 添加进购物车的是菜品，且 购物车中已经添加过 该菜品
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart oneCart = shoppingCartService.getOne(queryWrapper);
        //  如果购物车中 已经存在该菜品或套餐
        if (oneCart != null) {
            Integer number = oneCart.getNumber();
            // 如果数量大于 0，其数量 -1， 否则清除
            if (number != 0) {
                oneCart.setNumber(number - 1);
                shoppingCartService.updateById(oneCart);
            } else {
                shoppingCartService.remove(queryWrapper);
            }
        }
        return R.success("成功删减订单!");
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list();
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> cleanCart() {
        // 清除缓存
//        cleanCache();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // DELETE FROM shopping_cart WHERE (user_id = ?)
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("成功清空购物车！");
    }

    /**
     * 清除redis缓存
     */
//    public void cleanCache() {
//        String key = "shopping_" + BaseContext.getCurrentId();
//        redisTemplate.delete(key);
//    }


}

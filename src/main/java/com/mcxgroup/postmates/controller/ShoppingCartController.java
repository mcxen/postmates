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
        shoppingCart.setUserId(BaseContext.getCurrentId());
        log.info(shoppingCart.getName()+"购物车中的数据:{}" , shoppingCart.toString());
        if (shoppingCart.getSetmealId()!=null){
            LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());//这里是锁定了哪一个顾客
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());//这里是锁定哪一个菜
            ShoppingCart one = shoppingCartService.getOne(wrapper);
            if (one!=null){
                if (one.getNumber()>1){
                    one.setNumber(one.getNumber()-1);//减去分数
                    shoppingCartService.updateById(one);
                }else {
                    shoppingCartService.remove(wrapper);
                }
            }

        }
        if (shoppingCart.getDishId()!=null){
            LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());//这里是锁定了哪一个顾客
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());//这里是锁定哪一个菜
            ShoppingCart one = shoppingCartService.getOne(wrapper);
            if (one!=null){
                if (one.getNumber()>1){
                    one.setNumber(one.getNumber()-1);//减去分数
                    shoppingCartService.updateById(one);
                }else {
                    shoppingCartService.remove(wrapper);
                }
            }

        }

        return R.success("成功删减订单!");
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
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

package com.mcxgroup.postmates;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Test
    public void testRedis(){
        //通过opsForValue，申明对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("city","shanghai");
        System.out.println(valueOperations.get("city"));
        //Hash结构
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put("china","hubei","wuhan");
        hashOperations.put("china","hunan","xiangtan");
        System.out.println(hashOperations.get("china", "hunan"));
        Set<Object> keys = hashOperations.keys("china");//得到Hash的每一个属性
        for (Object key : keys) {
            System.out.println(key + " = "+hashOperations.get("china",key));
        }
    }
}

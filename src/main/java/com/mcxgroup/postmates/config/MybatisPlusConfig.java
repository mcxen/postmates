package com.mcxgroup.postmates.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: MybatisPlus配置类
 * @author: MCXEN
 * @date: 2022/11/26
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * @Description: 配置mybatis-plus分页插件
     * @Author: MCXEN
     */
    @Bean
//    拦截器
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());//页面
        return mybatisPlusInterceptor;
    }
}

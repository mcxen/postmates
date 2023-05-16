package com.mcxgroup.postmates.config;

import com.mcxgroup.postmates.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /*
     * @Description: 静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源开始映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/"); // 后台
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");     // 前台
    }

    /**
     * @Description: 扩展mvc框架的消息转换器,主要的目的是将long转换成string，以便完整的记录long型的id
     * @param converters
     * @Author: MCXEN
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("使用自定义消息转换器");
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        // 设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        // 将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }


}

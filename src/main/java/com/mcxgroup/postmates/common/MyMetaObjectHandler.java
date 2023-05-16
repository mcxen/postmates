package com.mcxgroup.postmates.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段更新update");

        log.info(metaObject.toString());
    }
}
package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.dto.DishDto;
import com.mcxgroup.postmates.entity.Category;
import com.mcxgroup.postmates.entity.Dish;
import com.mcxgroup.postmates.service.CategoryService;
import com.mcxgroup.postmates.service.DishFlavorService;
import com.mcxgroup.postmates.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    //
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    //新增方法
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        //服务端接口需要使用 @RequestBody 注解，告诉 Spring MVC 框架该方法需要从请求体中读取数据，
        // 并将其转换为 Java 对象。
        //需要保存： 口味和Dish菜品
        log.info(dishDto.toString());//参数正确了
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageDto = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        //返回一个category的Name而不是Id
        BeanUtils.copyProperties(pageInfo,pageDto,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        pageDto.setRecords(list);
        return R.success(pageDto);
    }

}

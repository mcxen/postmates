package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcxgroup.postmates.dto.DishDto;
import com.mcxgroup.postmates.service.CategoryService;
import com.mcxgroup.postmates.service.DishFlavorService;
import com.mcxgroup.postmates.entity.DishFlavor;
import com.mcxgroup.postmates.service.DishService;
import com.mcxgroup.postmates.entity.Dish;
import com.mcxgroup.postmates.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryService categoryService;
    /**
     * @Description: 根据id查菜品信息和对应的口味信息
     * @param id 菜品id
     */
    @Override
    public DishDto getByIdWithFlavor(String id) {
        // 返回值dishDto
        DishDto dishDto = new DishDto();

        // 查询的菜品
        Dish dish = this.getById(id);

        // 查询菜品对应的口味列表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        // 将菜品对应的属性赋值给返回对象
        BeanUtils.copyProperties(dish,dishDto);
        // 将口味列表赋值给返回对象
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * @Description: 新建菜品和口味
     * @param dishDto 封装实体
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto){
        this.save(dishDto);//直接就可以保存基本的信息了，dsishDTO是子类
        Long dishDtoId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);//口味表只有name没有dishId
    }
}
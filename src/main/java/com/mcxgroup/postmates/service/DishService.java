package com.mcxgroup.postmates.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mcxgroup.postmates.dto.DishDto;
import com.mcxgroup.postmates.entity.Dish;

public interface DishService extends IService<Dish> {
    DishDto getByIdWithFlavor(String id);
    void saveWithFlavor(DishDto dishDto);
    void updateWithFlavor(DishDto dishDto);

}

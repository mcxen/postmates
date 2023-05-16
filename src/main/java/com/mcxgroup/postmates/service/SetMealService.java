package com.mcxgroup.postmates.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mcxgroup.postmates.dto.SetMealDto;
import com.mcxgroup.postmates.entity.SetMeal;

import java.util.List;

public interface SetMealService extends IService<SetMeal> {

    SetMealDto getByIdWithSetMealDto(String id);

    boolean updateWithDish(SetMealDto setMealDto);

    boolean saveByIdWithSetMealDto(SetMealDto setMealDto);

    List<SetMeal> getListByCategoryIdWithSetMeal(String categoryId, Integer status);
}

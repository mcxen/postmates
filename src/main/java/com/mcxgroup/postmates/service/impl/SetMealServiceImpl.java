package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcxgroup.postmates.common.CustomException;
import com.mcxgroup.postmates.dto.SetMealDto;
import com.mcxgroup.postmates.service.CategoryService;
import com.mcxgroup.postmates.entity.SetMealDish;
import com.mcxgroup.postmates.service.SetMealService;
import com.mcxgroup.postmates.entity.Category;
import com.mcxgroup.postmates.entity.SetMeal;
import com.mcxgroup.postmates.mapper.SetMealMapper;
import com.mcxgroup.postmates.service.SetMealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 套餐服务实现类
 * @author: CoderMast
 * @date: 2022/11/27
 * @Blog: <a href="https://www.codermast.com/">codermast</a>
 */
@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetMealDishService setMealDishService;

    @Override
    public SetMealDto getByIdWithSetMealDto(String id) {
        SetMeal setMeal = this.getById(id);

        SetMealDto setMealDto = new SetMealDto();
        BeanUtils.copyProperties(setMeal,setMealDto);

        Long categoryId = setMeal.getCategoryId();
        Category category = categoryService.getById(categoryId);
        setMealDto.setCategoryName(category.getName());

        LambdaQueryWrapper<SetMealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMealDish::getDishId,id);

        List<SetMealDish> setMealDishes = setMealDishService.list(queryWrapper);
        setMealDto.setSetmealDishes(setMealDishes);
        return setMealDto;
    }

    @Override
    @Transactional
    public boolean updateWithDish(SetMealDto setMealDto) {
        this.updateById(setMealDto);
        List<SetMealDish> setmealDishes = setMealDto.getSetmealDishes();
        setmealDishes.forEach((item)->{
            item.setSetmealId(setMealDto.getId());
        });
        log.info(setmealDishes.toString());
        setMealDishService.saveOrUpdateBatch(setmealDishes);
        return true;
    }

    @Override
    @Transactional
    public boolean saveByIdWithSetMealDto(SetMealDto setMealDto) {
        this.save(setMealDto);
        List<SetMealDish> setmealDishes = setMealDto.getSetmealDishes();
        List<SetMealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setMealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(collect);
        return true;
    }

    @Override
    public List<SetMeal> getListByCategoryIdWithSetMeal(String categoryId, Integer status) {
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMeal::getCategoryId,categoryId);
        queryWrapper.eq(status != null,SetMeal::getStatus,status);
        return this.list(queryWrapper);
    }

    @Override
    public boolean removeWithDish(List<Long> ids) {
        //查询状态
        LambdaQueryWrapper<SetMeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetMeal::getId,ids);
        wrapper.eq(SetMeal::getStatus,1);
        int count = this.count(wrapper);//得到计数
        //可以删除就删除套餐的数据 -- setmeal
        if (count>0){
            throw new CustomException("套餐正在售卖");
        //    不能删除
        }
        this.removeByIds(ids);

        //删除关系表的数据 SetMeal_Dish
        LambdaQueryWrapper<SetMealDish> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.in(SetMealDish::getSetmealId,ids);
        setMealDishService.remove(wrapper1);
        return true;
    }

}

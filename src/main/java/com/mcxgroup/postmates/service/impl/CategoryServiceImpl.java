package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.mcxgroup.postmates.common.CustomException;
import com.mcxgroup.postmates.common.CustomException;
import com.mcxgroup.postmates.entity.Dish;
import com.mcxgroup.postmates.entity.SetMeal;
import com.mcxgroup.postmates.mapper.CategoryMapper;
import com.mcxgroup.postmates.service.CategoryService;
import com.mcxgroup.postmates.service.DishService;
import com.mcxgroup.postmates.service.SetMealService;
import com.mcxgroup.postmates.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    // 注入 dishService 对象
    @Autowired
    private DishService dishService;

    // 注入 setMealService 对象
    @Autowired
    private SetMealService setMealService;

    /**
     * @param categoryId 分类的id
     * @Description: 通过分类id删除分类
     * @Author: <a href="https://www.codermast.com/">CoderMast</a>
     */
    @Override
    public void remove(Long categoryId) {
        // dish查询信息
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, categoryId);
        // setmeal查询信息
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(SetMeal::getCategoryId,categoryId);
        // dish表中的分类id和给到的分类id相等时的返回结果条数。
        long countDish = dishService.count(dishLambdaQueryWrapper);
        // setmeal表中的分类id和给定的分类id相等时的返回条数。
        long countSetMeal = setMealService.count(setMealLambdaQueryWrapper);

        // 有关联的菜品记录，则不能删除，抛出业务异常
        if (countDish > 0){
            throw new CustomException("当前的分类下存在相关联的菜品，无法删除");
        }

        // 有关联的套餐记录，则不能删除，抛出业务异常
        if (countSetMeal > 0){
            throw new CustomException("当前的分类下存在相关联的套餐，无法删除");
        }

        // 即没有关联的菜品，也没有关联的套餐。则可以删除。
        super.removeById(categoryId);
    }
}

package com.mcxgroup.postmates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mcxgroup.postmates.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}

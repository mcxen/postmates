package com.mcxgroup.postmates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mcxgroup.postmates.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

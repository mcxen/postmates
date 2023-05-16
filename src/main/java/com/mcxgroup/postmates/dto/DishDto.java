package com.mcxgroup.postmates.dto;

import com.mcxgroup.postmates.entity.Dish;
import com.mcxgroup.postmates.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}

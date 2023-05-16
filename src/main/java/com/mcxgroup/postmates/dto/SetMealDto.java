package com.mcxgroup.postmates.dto;

import com.mcxgroup.postmates.entity.SetMeal;
import com.mcxgroup.postmates.entity.SetMealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetMealDto extends SetMeal {

    private List<SetMealDish> setmealDishes;

    private String categoryName;
}

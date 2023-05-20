package com.mcxgroup.postmates.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mcxgroup.postmates.dto.SetMealDto;
import com.mcxgroup.postmates.entity.SetMeal;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SetMealService extends IService<SetMeal> {

    SetMealDto getByIdWithSetMealDto(String id);

    boolean updateWithDish(SetMealDto setMealDto);

    /**
     * 实际就是保存dto数据，：保存setmeal，保存其他信息
     * @param setMealDto
     * @return
     */
    boolean saveByIdWithSetMealDto(SetMealDto setMealDto);

    List<SetMeal> getListByCategoryIdWithSetMeal(String categoryId, Integer status);

    boolean removeWithDish(List<Long> ids);
}

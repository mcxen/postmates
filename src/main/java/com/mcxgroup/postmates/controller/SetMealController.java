package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.dto.DishDto;
import com.mcxgroup.postmates.dto.SetMealDto;
import com.mcxgroup.postmates.entity.Category;
import com.mcxgroup.postmates.entity.SetMeal;
import com.mcxgroup.postmates.service.CategoryService;
import com.mcxgroup.postmates.service.DishService;
import com.mcxgroup.postmates.service.SetMealDishService;
import com.mcxgroup.postmates.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetMealDto setMealDto){
        log.info("套餐信息：{}",setMealDto.toString());
        setMealService.saveByIdWithSetMealDto(setMealDto);
        return null;
    }

    /**
     * @Description: 分页获取套餐信息
     * @param page 页码
     * @param pageSize 页面大小
     * @param name 关键词
     */
    @GetMapping("/page")
    public R<Page<SetMealDto>> page(int page, int pageSize, String name){
        //实现套餐分页
        //page
        Page<SetMeal> setMealPage = new Page<>(page, pageSize);
        Page<SetMealDto> setMealDtoPage = new Page<>();
        // lambda查询条件
        LambdaQueryWrapper<SetMeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),SetMeal::getName,name);//条件，比较的对象，就是查询得到的getName，与name
        setMealService.page(setMealPage,wrapper);//调用setMealService服务的page方法。
        // 该方法使用setMealPage对象分页查询SetMeal对象，并将wrapper中的查询条件应用于查询中。
        List<SetMeal> setMeals = setMealPage.getRecords();//得到了套餐的列表
        //处理套餐列表，使得符合需要的setMealDto
        // Dto包括：继承->来的setMeal，以及setmealDishes，以及categoryName
        List<SetMealDto> setMealDtos = setMeals.stream().map((item) -> {
            SetMealDto setMealDto = new SetMealDto();
            BeanUtils.copyProperties(item, setMealDto);//Setmeal对象套餐的属性赋值给SetmealDto的基本的信息
            Long categoryId = setMealDto.getCategoryId();
            Category category = categoryService.getById(categoryId);//得到这个套餐的category的对象
            setMealDto.setCategoryName(category.getName());
            return setMealDto;
        }).collect(Collectors.toList());
        setMealDtoPage.setTotal(setMealPage.getTotal());//把查询得到的原来的套餐的总数赋值给了新的Dto的总数
        setMealDtoPage.setRecords(setMealDtos);//这个Dto的Page的元素都有了，一个是total一个是Records
        return R.success(setMealDtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        //http://localhost:8080/setmeal?ids=1415580119015145474
        //删除的参数是ids是HTTP请求参数.
        setMealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{statu}")
    public R<String> status(@RequestParam("ids") List<Long> ids,@PathVariable Integer statu){
        log.info("更改套餐销售状态{}",statu);
        LambdaQueryWrapper<SetMeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ids!=null,SetMeal::getId,ids);
        List<SetMeal> updateList = setMealService.list(wrapper);
        for (SetMeal setMeal : updateList) {
            if (setMeal!=null){
                setMeal.setStatus(statu);
                setMealService.updateById(setMeal);
            }
        }
        return R.success(statu==1?"启动成功":"禁售成功");
    }
    @GetMapping("/dish/{id}")
    public R<DishDto> dish(@PathVariable String id){
        return R.success(dishService.getByIdWithFlavor(id));
    }

    @GetMapping("/list")
    public R<List<SetMeal>> list(SetMeal setmeal){
        log.info("setmeal获取list{}:",setmeal);
        //条件构造器
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(setmeal.getName()), SetMeal::getName, setmeal.getName());
        queryWrapper.eq(null != setmeal.getCategoryId(), SetMeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(null != setmeal.getStatus(), SetMeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(SetMeal::getUpdateTime);

        return R.success(setMealService.list(queryWrapper));
    }

}

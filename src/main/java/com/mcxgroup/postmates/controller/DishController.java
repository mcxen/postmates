package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.dto.DishDto;
import com.mcxgroup.postmates.entity.Category;
import com.mcxgroup.postmates.entity.Dish;
import com.mcxgroup.postmates.service.CategoryService;
import com.mcxgroup.postmates.service.DishFlavorService;
import com.mcxgroup.postmates.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    //
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    //新增方法
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        //服务端接口需要使用 @RequestBody 注解，告诉 Spring MVC 框架该方法需要从请求体中读取数据，
        // 并将其转换为 Java 对象。
        //需要保存： 口味和Dish菜品
        log.info(dishDto.toString());//参数正确了
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageDto = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        //返回一个category的Name而不是Id
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        pageDto.setRecords(list);
        pageDto.setTotal(pageInfo.getTotal());
        return R.success(pageDto);
    }


//    @GetMapping("/page")
//    public R<Page> page(int page, int pageSize, String name) {
//        // 菜品分页页面
//        Page<Dish> dishPage = new Page<>(page,pageSize);
//        // 菜品分页交互对象页面
//        Page<DishDto> dishDtoPage = new Page<>();
//
//        // 构造条件过滤器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        // 构建查询条件
//        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
//        // 分页查询菜品
//        dishService.page(dishPage, queryWrapper);
//
//        // 菜品分页记录值
//        List<Dish> recordsDish = dishPage.getRecords();
//
//        // 菜品分页交互对象记录值
//        List<DishDto> recordsDishDto = recordsDish.stream().map((item) -> {
//            // 创建dishDto对象
//            DishDto dishDto = new DishDto();
//
//            // 将Dish类型的item属性赋值到dishDto
//            BeanUtils.copyProperties(item,dishDto);
//
//            // 获取对象的分类id
//            Long categoryId = dishDto.getCategoryId();
//
//            // 根据分类id查分类对象
//            Category categoryServiceById = categoryService.getById(categoryId);
//
//            // 从分类对象中取出分类名称
//            String categoryServiceByIdName = categoryServiceById.getName();
//
//            // 设置分类名称到dishDto对象
//            dishDto.setCategoryName(categoryServiceByIdName);
//            // 返回该对象
//            return dishDto;
//        }).collect(Collectors.toList());
//
//        // 将封装好的记录值赋值给dishDtoPage对象
//        dishDtoPage.setRecords(recordsDishDto);
//        // 将总条数赋值给dishDtoPage
//        dishDtoPage.setTotal(dishPage.getTotal());
//        return R.success(dishDtoPage);
//    }


//    @DeleteMapping
//    public R<String> delete(String ids) {
//        String[] split = ids.split(",");
//        List<String> list = new ArrayList<>(Arrays.asList(split));
//        dishService.removeBatchByIds(list);
//        return R.success("批量删除成功！");
//    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){//注释表示该方法参数将接收URL路径变量值。
        //由于这个id是在请求里面的path:/dish/1413384757047271425"
        //其中R表示响应对象，DishDto是传输数据对象(DTO)的类型。
        //根据id查询菜品信息和口味信息
        DishDto dishDto = dishService.getByIdWithFlavor(String.valueOf(id));
        log.info("返回菜品的dishDto的Flavors{}: "+dishDto.getFlavors().toString());
        return R.success(dishDto);
    }

    /**
     * @Description: 停售和启售
     * @param status : 状态码，0为停售，1为启售
     * @param ids 操作菜品的id列表
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);

        for (Dish dish : list) {
            if (dish != null){
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
        return R.success(status == 1? "启售成功" : "停售成功");
    }


    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        //服务端接口需要使用 @RequestBody 注解，告诉 Spring MVC 框架该方法需要从请求体中读取数据，
        // 并将其转换为 Java 对象。
        //需要保存： 口味和Dish菜品
        log.info(BaseContext.getCurrentEmpId()+"员工请求修改菜品,菜品信息为：{}",dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        return R.success(list);
    }

}

package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.entity.Category;
import com.mcxgroup.postmates.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    // 注入CategoryService
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 增加排序条件
        queryWrapper.orderByDesc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        log.info("Category的控制器，page正在分页进行：{}",pageInfo.toString());
        return R.success(pageInfo);
    }

    /**
     * @Description: 增加分类
     * @param category 分类封装对象
     *  category.name 分类名称
     *  category.sort 分类排序
     *  category.type 分类类型 1为菜品类型 2为套餐类型
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Category category){
        Long id = (Long) request.getSession().getAttribute("employee");
        if (category == null){
            return R.error("类型为空");
        }
//        BaseContext.setCurrentId(id);
        categoryService.save(category);
        return R.success("创建成功");
    }

    /**
     * @Description: 根据id删除分类
     * @param ids 分类id
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        // 直接通过id进行删除，未判断是否含有所关联的dish内容
        //categoryService.removeById(ids);
        // 对于上述的优化
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * @Description: 更新分类信息
     * @param category 更新分类封装对象
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Category category){
        if (category == null){
            return R.error("类型为空");
        }
        Long id = (Long) request.getSession().getAttribute("employee");

//        BaseContext.setCurrentId(id);
        categoryService.updateById(category);
        return R.success("更新成功");
    }

    /**
     * @Description: 获取菜品的分类类列表
     * @param type 类型
     */
    @GetMapping("/list")
    public R<List<Category>> list(Integer type){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(type != null,Category::getType,type);
        queryWrapper.orderByDesc(Category::getSort);
        queryWrapper.orderByAsc(Category::getCreateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}

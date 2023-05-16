package com.mcxgroup.postmates.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mcxgroup.postmates.entity.Category;

/**
 * @Description: 分类服务接口
 * @author: MCXEN
 * @date: 2022/11/27
 * MCXEN
 */
public interface CategoryService extends IService<Category> {
    void remove(Long id);
}

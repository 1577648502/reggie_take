package com.lfg.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lfg.entity.Category;
import com.lfg.entity.Employee;


public interface CategoryService extends IService<Category> {
    public void remove(Long id);

}

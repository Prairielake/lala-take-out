package com.lala.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lala.dto.CategoryDTO;
import com.lala.dto.CategoryPageQueryDTO;
import com.lala.entity.Category;
import com.lala.entity.Employee;
import com.lala.result.PageResult;
import com.lala.result.Result;

import java.util.List;

public interface CategoryService extends IService<Category> {

    void save(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void deleteById(Long id);

    Boolean update(CategoryDTO categoryDTO);

    void startOrStop(Integer status, Long id);

    List<Category> list(Integer type);
}

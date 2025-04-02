package com.lala.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.lala.constant.MessageConstant;
import com.lala.constant.StatusConstant;
import com.lala.context.BaseContext;
import com.lala.dto.CategoryDTO;
import com.lala.dto.CategoryPageQueryDTO;
import com.lala.entity.Category;
import com.lala.entity.Dish;
import com.lala.entity.Setmeal;
import com.lala.exception.DeletionNotAllowedException;
import com.lala.mapper.CategoryMapper;
import com.lala.mapper.DishMapper;
import com.lala.mapper.SetmealMapper;
import com.lala.result.PageResult;
import com.lala.result.Result;
import com.lala.service.CategoryService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        this.save(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
//        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page = new Page<>(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(categoryPageQueryDTO.getName())) {
            queryWrapper.like(Category::getName, categoryPageQueryDTO.getName());
        }
        if (categoryPageQueryDTO.getType() != null) {
            queryWrapper.eq(Category::getType, categoryPageQueryDTO.getType());
        }
        queryWrapper.orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime);
        Page<Category> pageResult = categoryMapper.selectPage(page, queryWrapper);

        // 构造返回结果
        PageResult result = new PageResult();
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    @Override
    public void deleteById(Long id) {
        Long countDish = dishMapper.selectCount(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id));
        Long countSetmeal = setmealMapper.selectCount(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, id));
        if(countDish > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        if(countSetmeal > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public Boolean update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.ENABLE);
        category.setUpdateUser(BaseContext.getCurrentId());
        boolean isSuccess = updateById(category);
        return BooleanUtils.isTrue(isSuccess);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.updateById(category);
    }

    @Override
    public List<Category> list(Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getType, type);
        return categoryMapper.selectList(queryWrapper);
    }


}

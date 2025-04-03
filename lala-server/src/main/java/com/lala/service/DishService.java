package com.lala.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lala.dto.DishDTO;
import com.lala.dto.DishPageQueryDTO;
import com.lala.entity.Dish;
import com.lala.result.PageResult;
import com.lala.vo.DishVO;

import java.util.List;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void updateStatus(Integer status, Long id);

    DishVO getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    List<DishVO> listByCategoryId(String categoryId);
}

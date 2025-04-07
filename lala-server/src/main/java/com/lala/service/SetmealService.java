package com.lala.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lala.dto.SetmealDTO;
import com.lala.dto.SetmealPageQueryDTO;
import com.lala.entity.Setmeal;
import com.lala.result.PageResult;
import com.lala.vo.DishItemVO;
import com.lala.vo.SetmealVO;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getByIdWithDish(Long id);

    void updateSetmeal(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);

    void deleteBatch(List<Long> ids);

    List<Setmeal> listByCategoryId(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);
}

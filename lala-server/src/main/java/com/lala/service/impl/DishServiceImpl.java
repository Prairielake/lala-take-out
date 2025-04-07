package com.lala.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lala.constant.MessageConstant;
import com.lala.constant.StatusConstant;
import com.lala.dto.DishDTO;
import com.lala.dto.DishPageQueryDTO;
import com.lala.entity.Dish;
import com.lala.entity.DishFlavor;
import com.lala.entity.SetmealDish;
import com.lala.exception.DeletionNotAllowedException;
import com.lala.mapper.DishFlavorMapper;
import com.lala.mapper.DishMapper;
import com.lala.mapper.SetmealDishMapper;
import com.lala.mapper.SetmealMapper;
import com.lala.result.PageResult;
import com.lala.service.DishService;
import com.lala.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;


    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
                dishFlavorMapper.insert(dishFlavor);
            });
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        Page<Dish> page = new Page<>(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(dishPageQueryDTO.getName() != null, Dish::getName, dishPageQueryDTO.getName());
        lambdaQueryWrapper.like(dishPageQueryDTO.getCategoryId() != null, Dish::getCategoryId, dishPageQueryDTO.getCategoryId());
        lambdaQueryWrapper.like(dishPageQueryDTO.getStatus() != null, Dish::getStatus, dishPageQueryDTO.getStatus());
        Page<Dish> pageResult = dishMapper.selectPage(page, lambdaQueryWrapper);
        List<DishVO> dishVOList = pageResult.getRecords().stream()
                .map(dish -> BeanUtil.copyProperties(dish, DishVO.class))
                .toList();
        return new PageResult(pageResult.getTotal(), dishVOList);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SetmealDish::getDishId, dish.getId());
            Long count = setmealDishMapper.selectCount(lambdaQueryWrapper);
            if (count > 0) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
            dishMapper.deleteById(id);
            dishFlavorMapper.delete(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        }
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.updateById(dish);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.selectById(id);
        DishVO dishVO = BeanUtil.copyProperties(dish, DishVO.class);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavor = dishFlavorMapper.selectList(lambdaQueryWrapper);
        dishVO.setFlavors(dishFlavor);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);
    }

    @Override
    public List<DishVO> listByCategoryId(String categoryId) {
        List<Dish> dishes = dishMapper.selectList(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, categoryId));
        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish dish : dishes) {
            DishVO dishVO = BeanUtil.copyProperties(dish, DishVO.class);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        List<Dish> dishList = dishMapper.selectList(lambdaQueryWrapper);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(DishFlavor::getDishId, d.getId());
            List<DishFlavor> flavors = dishFlavorMapper.selectList(lambdaQueryWrapper1);

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}

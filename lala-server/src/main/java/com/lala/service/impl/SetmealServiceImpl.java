package com.lala.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lala.constant.MessageConstant;
import com.lala.constant.StatusConstant;
import com.lala.context.BaseContext;
import com.lala.dto.SetmealDTO;
import com.lala.dto.SetmealPageQueryDTO;
import com.lala.entity.Dish;
import com.lala.entity.Setmeal;
import com.lala.entity.SetmealDish;
import com.lala.exception.DeletionNotAllowedException;
import com.lala.mapper.SetmealDishMapper;
import com.lala.mapper.SetmealMapper;
import com.lala.result.PageResult;
import com.lala.service.SetmealService;
import com.lala.vo.DishItemVO;
import com.lala.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
//        setmeal.setStatus(StatusConstant.ENABLE);
//        setmeal.setCreateUser(BaseContext.getCurrentId());
//        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            setmealDishMapper.insert(setmealDish);
        });
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        Page<Setmeal> page = new Page<>(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(setmealPageQueryDTO.getName() != null, Setmeal::getName, setmealPageQueryDTO.getName());
        lambdaQueryWrapper.like(setmealPageQueryDTO.getStatus() != null, Setmeal::getStatus, setmealPageQueryDTO.getStatus());
        lambdaQueryWrapper.like(setmealPageQueryDTO.getCategoryId() != null, Setmeal::getCategoryId, setmealPageQueryDTO.getCategoryId());
        Page<Setmeal> setmealPage = setmealMapper.selectPage(page, lambdaQueryWrapper);
        List<SetmealVO> setmealVOList = setmealPage.getRecords().stream()
                .map(setmeal -> BeanUtil.copyProperties(setmeal, SetmealVO.class))
                .toList();
        return new PageResult(setmealPage.getTotal(), setmealVOList);
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(lambdaQueryWrapper);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.updateById(setmeal);
        //删除套餐关系，重新插入
        Long setmealId = setmealDTO.getId();
        setmealDishMapper.delete(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, setmealId));
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            setmealDishMapper.insert(setmealDish);
        });
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        setmealMapper.updateById(setmeal);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if (Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
            setmealMapper.deleteById(id);
            setmealDishMapper.delete(lambdaQueryWrapper);
        }
    }

    @Override
    public List<Setmeal> listByCategoryId(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        return setmealMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }


}

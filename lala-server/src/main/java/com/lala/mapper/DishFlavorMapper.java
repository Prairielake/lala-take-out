package com.lala.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lala.dto.DishDTO;
import com.lala.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}

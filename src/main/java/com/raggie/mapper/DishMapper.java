package com.raggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.raggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}

package com.raggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.raggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

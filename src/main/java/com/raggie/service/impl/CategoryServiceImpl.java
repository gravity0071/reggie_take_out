package com.raggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.raggie.common.CustomException;
import com.raggie.entity.Category;
import com.raggie.entity.Dish;
import com.raggie.entity.Setmeal;
import com.raggie.mapper.CategoryMapper;
import com.raggie.service.CategoryService;
import com.raggie.service.DishService;
import com.raggie.service.SetmealService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    //search whether there has dish or meal set under the category, if true, throws a exception
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count > 0) {
            throw new CustomException("has dish in the category, can't delete");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1 > 0){
            throw new CustomException("has meal set in the category, can't delete");
        }
        super.removeById(id);
    }
}

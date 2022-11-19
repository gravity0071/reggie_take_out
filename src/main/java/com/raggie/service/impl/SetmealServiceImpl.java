package com.raggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.raggie.common.CustomException;
import com.raggie.dto.SetmealDto;
import com.raggie.entity.Setmeal;
import com.raggie.entity.SetmealDish;
import com.raggie.mapper.SetmealMapper;
import com.raggie.service.SetmealDishService;
import com.raggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishServicel;


    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishServicel.saveBatch(setmealDishes);

    }

    @Override
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus, 1)
                .in(Setmeal::getId, ids);
        int count = this.count(queryWrapper);
        if(count > 0){
            throw new CustomException("can't delete in sale meal set");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishServicel.remove(lambdaQueryWrapper);
    }
}

package com.raggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.raggie.common.CustomException;
import com.raggie.common.R;
import com.raggie.dto.DishDto;
import com.raggie.entity.Category;
import com.raggie.entity.Dish;
import com.raggie.entity.DishFlavor;
import com.raggie.service.CategoryService;
import com.raggie.service.DishFlavorService;
import com.raggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * dish control
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("insert success!");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        // inside pageInfo, there has a record parameter to store all the dishes,from the dish table
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage =  new Page<>();

        //search the dish table to get all the dishes
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);

        //after searching the dish, we convert it to dishDTO class, to demonstrate the category
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");

        //set records manually for we need search the category table and set it into the
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list =  records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Category category = categoryService.getById(item.getCategoryId());

            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);

        return R.success("insert success!");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> list1 = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //first get the data from redis
        list1 = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(list1 != null){
            return R.success(list1);
        }
        //the data doesn't exist in redis


        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        list1 =  list.stream().map(item->{
           DishDto dishDto = new DishDto();
           BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if(byId != null){
                String name = byId.getName();
                dishDto.setCategoryName(name);
            }
            Long id = item.getId();
            List<DishFlavor> list2 = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
            dishDto.setFlavors(list2);
            return dishDto;
        }).collect(Collectors.toList());
        //add the data to redis and set expire time for an hour
        redisTemplate.opsForValue().set(key, list1, 60, TimeUnit.MINUTES);
        return R.success(list1);
    }

    @PostMapping("/status/{status}")
    public R<String> change(@RequestParam List<Long> ids, @PathVariable("status") int status){
        if(ids == null) throw new CustomException("can't select nothing");
        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids)
                .set(Dish::getStatus, status);
        dishService.update(queryWrapper);
        return R.success("modify success!");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("delete success!");
    }


}

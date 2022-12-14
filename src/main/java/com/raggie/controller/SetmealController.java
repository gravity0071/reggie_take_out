package com.raggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.raggie.common.CustomException;
import com.raggie.common.R;
import com.raggie.dto.SetmealDto;
import com.raggie.entity.Category;
import com.raggie.entity.Setmeal;
import com.raggie.service.CategoryService;
import com.raggie.service.SetmealDishService;
import com.raggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //clear all setmealCache
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("insert success!");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> dtoPage = new Page<>();


        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list =records.stream().map(item->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if(byId != null){
                String idName = byId.getName();
                setmealDto.setCategoryName(idName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true) //clear all setmealCache
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("delete success!");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true) //clear all setmealCache
    public R<String> change(@RequestParam List<Long> ids, @PathVariable("status") int status){
        if(ids == null) throw new CustomException("can't select nothing");
        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.in(ids != null, Setmeal::getId, ids)
                        .set(Setmeal::getStatus, status);
        setmealService.update(queryWrapper);
        return R.success("modify success!");
    }

    @GetMapping("/list")
    //enable caching
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    @GetMapping("/{id}")
    public R<Setmeal> getDetail(@PathVariable("id") Long id){
        Setmeal byId = setmealService.getById(id);
        return R.success(byId);
    }
}

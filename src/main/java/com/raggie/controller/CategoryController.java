package com.raggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.raggie.common.R;
import com.raggie.entity.Category;
import com.raggie.service.CategoryService;
import com.raggie.service.DishService;
import com.raggie.service.SetmealService;
import com.raggie.service.impl.DishServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category: " + category);
        categoryService.save(category);
        return R.success("add success!");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    //recieve id on the url, so there has no need to add @RequestBody to the parameter
    public R<String> delete(Long ids){
        log.info("prepare deleting: {}", ids);

        categoryService.remove(ids);
        return R.success("delete success");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("modify: {}" , category);
        categoryService.updateById(category);
        return R.success("modify success!");
    }

    /**
     * search for the dish_type list
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType())
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);

    }

}

package com.raggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.raggie.dto.DishDto;
import com.raggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    /**
     * insert a dish and insert flavor to another table
     */
    public void saveWithFlavor(DishDto dishDto);
    public void updateWithFlavor(DishDto dishDto);
    public void deleteWithFlavor(List<Long> ids);
    public DishDto getByIdWithFlavor(Long id);
}

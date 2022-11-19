package com.raggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.raggie.dto.SetmealDto;
import com.raggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void removeWithDish(List<Long> ids);
}

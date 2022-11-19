package com.raggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.raggie.entity.OrderDetail;
import com.raggie.entity.Orders;
import com.raggie.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    public void again(Orders list);
}

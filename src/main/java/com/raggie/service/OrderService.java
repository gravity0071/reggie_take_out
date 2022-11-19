package com.raggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.raggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}

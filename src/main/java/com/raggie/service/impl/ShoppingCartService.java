package com.raggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.raggie.common.BaseContext;
import com.raggie.entity.OrderDetail;
import com.raggie.entity.Orders;
import com.raggie.entity.ShoppingCart;
import com.raggie.mapper.ShoppingCartMapper;
import com.raggie.service.OrderDetailService;
import com.raggie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartService extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements com.raggie.service.ShoppingCartService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    @Transactional
    public void again(Orders orders) {
        List<OrderDetail> list = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, orders.getId()));
        for (OrderDetail orderDetail : list) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(BaseContext.getCurrentIdUser());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCart.setDishId(orderDetail.getDishId());
            shoppingCart.setSetmealId(orderDetail.getSetmealId());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
        }
    }
}

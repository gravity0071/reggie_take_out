package com.raggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.raggie.common.BaseContext;
import com.raggie.common.R;
import com.raggie.entity.ShoppingCart;
import com.raggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/shoppingCart")
@RestController
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //search whether the shopping exits the dish/ meal set
        Long currentId = BaseContext.getCurrentIdUser();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }
        else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one != null){
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        }
        else{
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    @GetMapping("list")
    public R<List<ShoppingCart>>  list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentIdUser())
                .orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return  R.success(list);
    }

    @DeleteMapping("clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentIdUser());
        shoppingCartService.remove(queryWrapper);
        return R.success("delete succeed");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one.getNumber() == 1){
            shoppingCartService.remove(queryWrapper);
        }
        else{
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }
        return R.success("sub succeed!");
    }
}

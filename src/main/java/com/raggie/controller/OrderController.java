package com.raggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.raggie.common.BaseContext;
import com.raggie.common.R;
import com.raggie.dto.OrdersDto;
import com.raggie.entity.OrderDetail;
import com.raggie.entity.Orders;
import com.raggie.entity.ShoppingCart;
import com.raggie.service.OrderDetailService;
import com.raggie.service.OrderService;
import com.raggie.service.OrdersDtoService;
import com.raggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrdersDtoService ordersDtoService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("order succeed");
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(int page, int pageSize) {
        Page<OrdersDto> pageInfo = new Page<>(page, pageSize);

        List<OrdersDto> list = new ArrayList<>();
        list = ordersDtoService.getOrderDto(BaseContext.getCurrentIdUser());

        //convert list to page, for the need needs search two tables which are not easy to user myBatis Plus to solve
        int start = (int)((pageInfo.getCurrent() - 1) * pageInfo.getSize());
        int end = (int)((start + pageInfo.getSize()) > list.size() ? list.size() : (pageInfo.getSize() * pageInfo.getCurrent()));
        pageInfo.setRecords(new ArrayList<>());
        pageInfo.setTotal(list.size());
        if (pageInfo.getSize()*(pageInfo.getCurrent()-1) <= pageInfo.getTotal()) {
            pageInfo.setRecords(list.subList(start, end));
        }

        return R.success(pageInfo);
    }


    @GetMapping("/page")
    public R<Page<Orders>> Page(int page, int pageSize, Orders orders, @DateTimeFormat String beginTime, @DateTimeFormat String endTime) {
        log.info(page + " " + pageSize + " " + orders + " " + beginTime + " " + endTime);
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Long currentId = BaseContext.getCurrentIdUser();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(orders.getNumber() != null, Orders::getNumber, orders.getNumber())
                .between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);
        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> deliver(@RequestBody Orders orders) {
        orderService.updateById(orders);
        return R.success("deliver success!");
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        shoppingCartService.again(orders);
        return R.success("success");
    }
}


//[       OrdersDto(userName=null, phone=13646464646, address=t, consignee=test,orderDetails=[OrderDetail(id=null, name=毛氏红烧肉, orderId=1593535285340270593, dishId=1397850140982161409, setmealId=null, dishFlavor=不要蒜,中辣, number=null, amount=null, image=0a3b3288-3446-4420-bbff-f263d0c02d8e.jpg)]),
//        OrdersDto(userName=null, phone=13646464646, address=t, consignee=test,orderDetails=[OrderDetail(id=null, name=口味蛇, orderId=1593535285340270593, dishId=1397851668262465537, setmealId=null, dishFlavor=去冰, number=null, amount=null, image=0f4bd884-dc9c-4cf9-b59e-7d5958fec3dd.jpg)]),
//        OrdersDto(userName=null, phone=13646464646, address=t, consignee=test, orderDetails=[OrderDetail(id=null, name=儿童套餐A计划, orderId=1593535285340270593, dishId=null, setmealId=1415580119015145474, dishFlavor=null, number=null, amount=null, image=61d20592-b37f-4d72-a864-07ad5bb8f3bb.jpg)]),
//        OrdersDto(userName=null, phone=13646464646, address=t, consignee=test, orderDetails=[OrderDetail(id=null, name=鱼香炒鸡蛋, orderId=1593536015220469762, dishId=1397854865672679425, setmealId=null, dishFlavor=重辣, number=null, amount=null, image=0f252364-a561-4e8d-8065-9a6797a6b1d3.jpg)]),
//        OrdersDto(userName=null, phone=13646464646, address=t, consignee=test, orderDetails=[OrderDetail(id=null, name=鱼香炒鸡蛋, orderId=1593537169358598146, dishId=1397854865672679425, setmealId=null, dishFlavor=重辣, number=null, amount=null, image=0f252364-a561-4e8d-8065-9a6797a6b1d3.jpg)]),
//        OrdersDto(userName=null, phone=13646464646, address=t, consignee=test, orderDetails=[OrderDetail(id=null, name=口味蛇, orderId=1593537684154888194, dishId=1397851668262465537, setmealId=null, dishFlavor=去冰, number=null, amount=null, image=0f4bd884-dc9c-4cf9-b59e-7d5958fec3dd.jpg)]),
//        OrdersDto(userName=null, phone=13646464646, address=test, consignee=test, orderDetails=[OrderDetail(id=null, name=儿童套餐A计划, orderId=1593559425338093569, dishId=null, setmealId=1415580119015145474, dishFlavor=null, number=null, amount=null, image=61d20592-b37f-4d72-a864-07ad5bb8f3bb.jpg)])]
//[OrdersDto(id=null,number=1593535285340270593,status=3,userId=1593495517889835009,addressBookId=1593495578266841089,orderTime=2022-11-18T17:23:13,checkoutTime=2022-11-18T17:23:13,payMethod=1,amount=316.00,remark=,userName=null,phone=13646464646,address=t,consignee=test,orderDetails=[OrderDetail(id=null,name=毛氏红烧肉,orderId=1593535285340270593,dishId=1397850140982161409,setmealId=null,dishFlavor=不要蒜,中辣,number=null,amount=null,image=0a3b3288-3446-4420-bbff-f263d0c02d8e.jpg)]),OrdersDto(id=null,number=1593535285340270593,status=3,userId=1593495517889835009,addressBookId=1593495578266841089,orderTime=2022-11-18T17:23:13,checkoutTime=2022-11-18T17:23:13,payMethod=1,amount=316.00,remark=,userName=null,phone=13646464646,address=t,consignee=test,orderDetails=[OrderDetail(id=null,name=口味蛇,orderId=1593535285340270593,dishId=1397851668262465537,setmealId=null,dishFlavor=去冰,number=null,amount=null,image=0f4bd884-dc9c-4cf9-b59e-7d5958fec3dd.jpg)]),OrdersDto(id=null,number=1593535285340270593,status=3,userId=1593495517889835009,addressBookId=1593495578266841089,orderTime=2022-11-18T17:23:13,checkoutTime=2022-11-18T17:23:13,payMethod=1,amount=316.00,remark=,userName=null,phone=13646464646,address=t,consignee=test,orderDetails=[OrderDetail(id=null,name=儿童套餐A计划,orderId=1593535285340270593,dishId=null,setmealId=1415580119015145474,dishFlavor=null,number=null,amount=null,image=61d20592-b37f-4d72-a864-07ad5bb8f3bb.jpg)]),OrdersDto(id=null,number=1593536015220469762,status=2,userId=1593495517889835009,addressBookId=1593495578266841089,orderTime=2022-11-18T17:26:07,checkoutTime=2022-11-18T17:26:07,payMethod=1,amount=20.00,remark=test,userName=null,phone=13646464646,address=t,consignee=test,orderDetails=[OrderDetail(id=null,name=鱼香炒鸡蛋,orderId=1593536015220469762,dishId=1397854865672679425,setmealId=null,dishFlavor=重辣,number=null,amount=null,image=0f252364-a561-4e8d-8065-9a6797a6b1d3.jpg)]),OrdersDto(id=null,number=1593537169358598146,status=2,userId=1593495517889835009,addressBookId=1593495578266841089,orderTime=2022-11-18T17:30:42,checkoutTime=2022-11-18T17:30:42,payMethod=1,amount=20.00,remark=,userName=null,phone=13646464646,address=t,consignee=test,orderDetails=[OrderDetail(id=null,name=鱼香炒鸡蛋,orderId=1593537169358598146,dishId=1397854865672679425,setmealId=null,dishFlavor=重辣,number=null,amount=null,image=0f252364-a561-4e8d-8065-9a6797a6b1d3.jpg)]),OrdersDto(id=null,number=1593537684154888194,status=2,userId=1593495517889835009,addressBookId=1593495578266841089,orderTime=2022-11-18T17:32:45,checkoutTime=2022-11-18T17:32:45,payMethod=1,amount=168.00,remark=,userName=null,phone=13646464646,address=t,consignee=test,orderDetails=[OrderDetail(id=null,name=口味蛇,orderId=1593537684154888194,dishId=1397851668262465537,setmealId=null,dishFlavor=去冰,number=null,amount=null,image=0f4bd884-dc9c-4cf9-b59e-7d5958fec3dd.jpg)]),OrdersDto(id=null,number=1593559425338093569,status=2,userId=1593495517889835009,addressBookId=1593546889591025666,orderTime=2022-11-18T18:59:09,checkoutTime=2022-11-18T18:59:09,payMethod=1,amount=40.00,remark=,userName=null,phone=13646464646,address=test,consignee=test,orderDetails=[OrderDetail(id=null,name=儿童套餐A计划,orderId=1593559425338093569,dishId=null,setmealId=1415580119015145474,dishFlavor=null,number=null,amount=null,image=61d20592-b37f-4d72-a864-07ad5bb8f3bb.jpg)])]


//    for(int i = 0; i < pageInfo.getRecords().size(); i++){
//        pagee.getRecords().get(i).setId(pageInfo.getRecords().get(i).getId());
//        pagee.getRecords().get(i).setNumber(pageInfo.getRecords().get(i).getNumber());
//        pagee.getRecords().get(i).setStatus(pageInfo.getRecords().get(i).getStatus());
//        pagee.getRecords().get(i).setUserId(pageInfo.getRecords().get(i).getUserId());
//        pagee.getRecords().get(i).setAddressBookId(pageInfo.getRecords().get(i).getAddressBookId());
//        pagee.getRecords().get(i).setOrderTime(pageInfo.getRecords().get(i).getOrderTime());
//        pagee.getRecords().get(i).setCheckoutTime(pageInfo.getRecords().get(i).getCheckoutTime());
//        pagee.getRecords().get(i).setPayMethod(pageInfo.getRecords().get(i).getPayMethod());
//        pagee.getRecords().get(i).setAmount(pageInfo.getRecords().get(i).getAmount());
//        pagee.getRecords().get(i).setRemark(pageInfo.getRecords().get(i).getRemark());
//        pagee.getRecords().get(i).setUserName(pageInfo.getRecords().get(i).getUserName());
//        pagee.getRecords().get(i).setPhone(pageInfo.getRecords().get(i).getPhone());
//        pagee.getRecords().get(i).setAddress(pageInfo.getRecords().get(i).getAddress());
//        pagee.getRecords().get(i).setConsignee(pageInfo.getRecords().get(i).getConsignee());
//    }
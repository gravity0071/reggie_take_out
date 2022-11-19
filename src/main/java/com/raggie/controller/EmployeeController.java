package com.raggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.raggie.common.R;
import com.raggie.entity.Employee;
import com.raggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee") //when login, we will recieve /employee/**
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    /*
     * user login
     * decode password (md5)
     * check whether the employ is locked: status == 1;
     */
    @PostMapping("/login")
    //@RequestBody: recieve request body(only in Post methed, which is json) and convert to java class
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //encode the password through md5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //looking for the database
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if(emp == null) return R.error("no such user");
        if(!emp.getPassword().equals(password)) return R.error("wrong password");
        if(emp.getStatus() == 0) return R.error("the user has been freeze");

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
        //springboot will convert the R to json
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //clear the user id in the session
        request.getSession().removeAttribute("employee");
        return R.success("logout success");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("add new member: {}", employee.toString());
        //use md5 to set default password 123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser((Long)request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));


        //INSERT INTO employee ( id, username, name, password, phone, sex, id_number, create_time, update_time, create_user, update_user ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
        employeeService.save(employee);
        return R.success("register success");
    }

    @GetMapping("/page")
    //name is for search
    public R<Page> page(HttpServletRequest request, int page, int pageSize, String name){
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name)
                .orderByDesc(Employee::getUpdateTime)
                .notIn(Employee::getId, request.getSession().getAttribute("employee"));

        //SELECT id,username,name,password,phone,sex,id_number,status,create_time,update_time,create_user,update_user FROM employee ORDER BY update_time DESC LIMIT ? (pageSize)
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("modify success!");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id){
        Employee employee = employeeService.getById(id);
        if(employee != null)
            return R.success(employee);
        return R.error("no such user");
    }
}

package com.raggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
 * mybatis_plus, @TableField when update or insert, deploy the method
 * set public words
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        if(metaObject.hasSetter("updateTime")) {
            metaObject.setValue("updateTime", LocalDateTime.now());
        }
        if(metaObject.hasSetter("createUser") && BaseContext.getCurrentId() != null) {
            metaObject.setValue("createUser", BaseContext.getCurrentId());
        }
        if(metaObject.hasSetter("createUser") && BaseContext.getCurrentIdUser() != null) {
            metaObject.setValue("createUser", BaseContext.getCurrentIdUser());
        }
        if(metaObject.hasSetter("updateUser") && BaseContext.getCurrentId() != null) {
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
        }
        if(metaObject.hasSetter("updateUser") && BaseContext.getCurrentIdUser() != null) {
            metaObject.setValue("updateUser", BaseContext.getCurrentIdUser());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());

        if(metaObject.hasSetter("updateUser") && BaseContext.getCurrentId() != null)
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
        if(metaObject.hasSetter("updateUser") && BaseContext.getCurrentIdUser() != null)
            metaObject.setValue("updateUser", BaseContext.getCurrentIdUser());
    }
}

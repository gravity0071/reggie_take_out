package com.raggie.common;

/*
 * threadLocal utility, to save current user id
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    private static ThreadLocal<Long> threadLocal2 = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }

    public static void setCurrentIdUser(Long id){
        threadLocal2.set(id);
    }
    public static Long getCurrentIdUser(){
        return threadLocal2.get();
    }
}

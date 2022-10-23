package com.lfg.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登陆用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        System.out.println("hello word");
        return threadLocal.get();
    }

}

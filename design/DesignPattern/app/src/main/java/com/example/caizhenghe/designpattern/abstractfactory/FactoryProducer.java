package com.example.caizhenghe.designpattern.abstractfactory;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class FactoryProducer {
    public static <T> T getFactory(Class<? extends T> clazz){
        T obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }
}

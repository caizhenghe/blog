package com.example.caizhenghe.designpattern.factory;

import com.example.caizhenghe.designpattern.abstractfactory.AbstractFactory;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class ShapeFactory implements AbstractFactory{

    @Override
    public <T> T getColor(Class<? extends T> clazz) {
        return null;
    }
    @Override
    public <T> T getShape(Class<? extends T> clazz){
        T obj = null;
        try {
            obj = clazz.newInstance();
//             obj = Class.forName(clazz.getName()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

}

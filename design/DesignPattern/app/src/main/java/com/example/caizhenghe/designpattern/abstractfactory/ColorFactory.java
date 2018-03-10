package com.example.caizhenghe.designpattern.abstractfactory;

import com.example.caizhenghe.designpattern.factory.Shape;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class ColorFactory implements AbstractFactory {

    // 反射的劣势：外界随意传递一个类类型的对象进来都可以生成一个具体实现类的对象，不安全
    @Override
    public <T> T getColor(Class<? extends T> clazz){
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

    @Override
    public <T> T getShape(Class<? extends T> clazz) {
        return null;
    }


}

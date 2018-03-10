package com.example.caizhenghe.designpattern.abstractfactory;

import com.example.caizhenghe.designpattern.factory.Shape;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public interface AbstractFactory {
    <T> T getColor(Class<? extends T> clazz);
    <T> T getShape(Class<? extends T> clazz);

}

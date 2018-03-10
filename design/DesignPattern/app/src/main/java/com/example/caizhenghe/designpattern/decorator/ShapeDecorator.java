package com.example.caizhenghe.designpattern.decorator;

import com.example.caizhenghe.designpattern.factory.Shape;

/**
 * Created by caizhenghe on 2018/3/11.
 */

public class ShapeDecorator implements Shape {
    Shape mShape;

    public ShapeDecorator(Shape shape){
        mShape = shape;
    }
    @Override
    public void draw() {
        mShape.draw();
    }
}

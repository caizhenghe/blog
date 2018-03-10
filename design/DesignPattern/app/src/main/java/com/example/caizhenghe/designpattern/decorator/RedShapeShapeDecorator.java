package com.example.caizhenghe.designpattern.decorator;

import android.util.Log;

import com.example.caizhenghe.designpattern.factory.Shape;

/**
 * Created by caizhenghe on 2018/3/11.
 */

public class RedShapeShapeDecorator extends ShapeDecorator {
    public RedShapeShapeDecorator(Shape shape) {
        super(shape);
    }

    @Override
    public void draw() {
        super.draw();
        setRedBorder();
    }

    void setRedBorder(){
        Log.d("TAG", "BorderColor: Red");
    }
}

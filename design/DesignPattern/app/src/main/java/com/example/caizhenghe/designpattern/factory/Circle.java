package com.example.caizhenghe.designpattern.factory;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class Circle implements Shape {
    @Override
    public void draw() {
        Log.d("TAG", "Circle.draw()");
    }
}


package com.example.caizhenghe.designpattern.factory;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class Square implements Shape {
    @Override
    public void draw() {
        Log.d("TAG", "Square.draw()");
    }
}

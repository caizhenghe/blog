package com.example.caizhenghe.designpattern.abstractfactory;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class Blue implements Color {
    @Override
    public void fill() {
        Log.d("TAG", "Blue.fill");
    }
}

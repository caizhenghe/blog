package com.example.caizhenghe.designpattern.build;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class Cola extends ColdDrink {
    @Override
    public float price() {
        return 3.0f;
    }

    @Override
    public String name() {
        return "Cola";
    }
}

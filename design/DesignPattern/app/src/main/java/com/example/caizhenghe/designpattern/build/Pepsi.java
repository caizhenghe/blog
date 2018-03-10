package com.example.caizhenghe.designpattern.build;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class Pepsi extends ColdDrink {
    @Override
    public float price() {
        return 15.0f;
    }

    @Override
    public String name() {
        return "Pepsi";
    }
}

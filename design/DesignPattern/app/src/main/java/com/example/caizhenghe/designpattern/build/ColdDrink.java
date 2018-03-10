package com.example.caizhenghe.designpattern.build;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class ColdDrink implements Item {
    @Override
    public String name() {
        return "";
    }

    @Override
    public Packing packing() {
        return new Bottle();
    }

    @Override
    public float price() {
        return 0;
    }
}

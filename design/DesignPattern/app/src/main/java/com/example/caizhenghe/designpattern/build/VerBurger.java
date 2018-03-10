package com.example.caizhenghe.designpattern.build;


/**
 * Created by caizhenghe on 2018/3/10.
 */

public class VerBurger extends Burger {

    @Override
    public float price() {
        return 25.0f;
    }

    @Override
    public String name() {
        return "VerBurger";
    }
}

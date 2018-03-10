package com.example.caizhenghe.designpattern.single;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class    BetterSingle {
    // 饿汉式，容易产生垃圾对象
    private static BetterSingle single = new BetterSingle();

    private BetterSingle() {
    }

    public static BetterSingle getInstance() {
        return single;
    }

}

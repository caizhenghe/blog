package com.example.caizhenghe.designpattern.single;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class BetterLazySingle {
    static class BetterLazySingleHolder{
        private static final BetterLazySingle INSTANCE = new BetterLazySingle();
    }
    private BetterLazySingle(){}

    public static final BetterLazySingle getInstance(){
        return BetterLazySingleHolder.INSTANCE;
    }
}

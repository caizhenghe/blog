package com.example.caizhenghe.designpattern.single;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class NormalSingle {
    private volatile static NormalSingle single;


    private NormalSingle(){}

    public static NormalSingle getInstance(){
        if(single == null){
            synchronized (NormalSingle.class){
                if(single == null){
                    single = new NormalSingle();
                }
            }
        }
        return single;
    }
}

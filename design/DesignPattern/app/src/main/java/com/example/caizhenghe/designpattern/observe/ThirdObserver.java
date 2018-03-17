package com.example.caizhenghe.designpattern.observe;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class ThirdObserver implements Observer {
    public ThirdObserver(Subject subject){
        subject.attach(this);
    }
    @Override
    public void update(int state) {
        Log.d("TAG", "ThirdObserver:: update: state = " + state);
    }
}

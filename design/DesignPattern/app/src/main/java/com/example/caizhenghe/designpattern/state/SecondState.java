package com.example.caizhenghe.designpattern.state;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class SecondState implements State {
    @Override
    public void doAction(Context context) {
        Log.d("TAG", "this is in second state");
        context.setState(this);
    }

    @Override
    public String toString() {
        return "Second State";
    }
}

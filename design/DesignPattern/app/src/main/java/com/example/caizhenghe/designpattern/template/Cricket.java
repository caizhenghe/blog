package com.example.caizhenghe.designpattern.template;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class Cricket extends Game {
    @Override
    void initialize() {
        Log.d("TAG", "Cricket:: initialize");
    }

    @Override
    void startPlay() {
        Log.d("TAG", "Cricket:: startPlay");

    }

    @Override
    void endPlay() {
        Log.d("TAG", "Cricket:: endPlay");

    }
}

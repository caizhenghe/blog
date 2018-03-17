package com.example.caizhenghe.designpattern.template;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class FootBall extends Game {
    @Override
    void initialize() {
        Log.d("TAG", "FootBall:: initialize");
    }

    @Override
    void startPlay() {
        Log.d("TAG", "FootBall:: startPlay");

    }

    @Override
    void endPlay() {
        Log.d("TAG", "FootBall:: endPlay");

    }
}

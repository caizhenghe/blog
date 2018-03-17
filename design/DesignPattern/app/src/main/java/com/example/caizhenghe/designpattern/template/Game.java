package com.example.caizhenghe.designpattern.template;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public abstract class Game {
    abstract void initialize();
    abstract void startPlay();
    abstract void endPlay();

    public final void play(){
        initialize();

        startPlay();

        endPlay();
    }
}

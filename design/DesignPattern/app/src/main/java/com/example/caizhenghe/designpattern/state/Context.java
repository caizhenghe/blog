package com.example.caizhenghe.designpattern.state;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class Context {

    private State state = null;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}

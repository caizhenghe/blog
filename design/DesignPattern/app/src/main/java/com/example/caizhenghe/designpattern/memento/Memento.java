package com.example.caizhenghe.designpattern.memento;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class Memento {
    private String state;
    public Memento(String state){
        this.state = state;
    }

    public String getState(){
        return state;
    }
}

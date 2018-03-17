package com.example.caizhenghe.designpattern.memento;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class Originator {
    private String state;

    public void setState(String state){
        this.state = state;
    }

    public String getState(){
        return state;
    }

    public Memento saveStateToMemento(){
        return new Memento(state);
    }

    public void getStateFromMemento(Memento m){
        this.state = m.getState();
    }
}

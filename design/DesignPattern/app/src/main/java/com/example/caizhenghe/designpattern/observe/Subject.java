package com.example.caizhenghe.designpattern.observe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class Subject {
    private int state;
    List<Observer> mObservers = new ArrayList<>();
    public void attach(Observer observer){
        mObservers.add(observer);
    }

    public void setState(int state){
        this.state = state;
        notifyAllObservers();
    }

    public void notifyAllObservers(){
        for(int i = 0; i < mObservers.size(); i++){
            mObservers.get(i).update(state);
        }
    }
}

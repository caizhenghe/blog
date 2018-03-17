package com.example.caizhenghe.designpattern.memento;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class CareTaker {
    private List<Memento> mementoList;


    public CareTaker(){
        mementoList = new ArrayList<>();
    }
    public void add(Memento m){
        mementoList.add(m);
    }

    public Memento get(int index){
        return mementoList.get(index);
    }

}

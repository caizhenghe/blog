package com.example.caizhenghe.designpattern.visitor;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class ComputerPartDisplayVisitor implements ComputerPartVisitor {
    @Override
    public void visit(Mouse mouse) {
        Log.d("TAG", "Display mouse");
    }

    @Override
    public void visit(Keyboard keyboard) {
        Log.d("TAG", "Display keyboard");

    }

    @Override
    public void visit(Computer computer) {
        Log.d("TAG", "Display computer");

    }

    @Override
    public void visit(Monitor monitor) {
        Log.d("TAG", "Display monitor");

    }
}

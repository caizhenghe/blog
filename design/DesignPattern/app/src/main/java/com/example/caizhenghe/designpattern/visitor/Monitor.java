package com.example.caizhenghe.designpattern.visitor;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class Monitor implements ComputerPart {
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
}

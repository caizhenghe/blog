package com.example.caizhenghe.designpattern.visitor;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public interface ComputerPartVisitor {
    void visit(Mouse mouse);
    void visit(Keyboard keyboard);
    void visit(Computer computer);
    void visit(Monitor monitor);
}

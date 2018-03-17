package com.example.caizhenghe.designpattern.visitor;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public interface ComputerPart {
    void accept(ComputerPartVisitor visitor);
}

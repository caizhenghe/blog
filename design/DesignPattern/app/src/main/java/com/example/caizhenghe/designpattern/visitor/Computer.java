package com.example.caizhenghe.designpattern.visitor;

/**
 * Created by caizhenghe on 2018/3/17.
 */

public class Computer implements ComputerPart {
    ComputerPart parts[];

    public Computer(){
        parts = new ComputerPart[]{new Mouse(), new Monitor(), new Keyboard()};
    }
    @Override
    public void accept(ComputerPartVisitor visitor) {
        for(ComputerPart part : parts){
            part.accept(visitor);
        }
        visitor.visit(this);
    }
}

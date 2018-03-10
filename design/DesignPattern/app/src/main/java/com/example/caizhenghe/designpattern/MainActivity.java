package com.example.caizhenghe.designpattern;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.caizhenghe.designpattern.abstractfactory.AbstractFactory;
import com.example.caizhenghe.designpattern.abstractfactory.Blue;
import com.example.caizhenghe.designpattern.abstractfactory.Color;
import com.example.caizhenghe.designpattern.abstractfactory.ColorFactory;
import com.example.caizhenghe.designpattern.abstractfactory.FactoryProducer;
import com.example.caizhenghe.designpattern.abstractfactory.Green;
import com.example.caizhenghe.designpattern.abstractfactory.Red;
import com.example.caizhenghe.designpattern.build.Meal;
import com.example.caizhenghe.designpattern.factory.Circle;
import com.example.caizhenghe.designpattern.factory.Rectangle;
import com.example.caizhenghe.designpattern.factory.Shape;
import com.example.caizhenghe.designpattern.factory.ShapeFactory;
import com.example.caizhenghe.designpattern.factory.Square;
import com.example.caizhenghe.designpattern.single.BestSingle;
import com.example.caizhenghe.designpattern.single.BetterLazySingle;
import com.example.caizhenghe.designpattern.single.BetterSingle;
import com.example.caizhenghe.designpattern.single.NormalSingle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 工厂模式
//        Shape rec = new ShapeFactory().getShape(Rectangle.class);
//        Shape square = new ShapeFactory().getShape(Square.class);
//        Shape circle = new ShapeFactory().getShape(Circle.class);
//
//        rec.draw();
//        square.draw();
//        circle.draw();

        // 抽象工厂模式
        AbstractFactory shapeFactory = FactoryProducer.getFactory(ShapeFactory.class);
        Shape rec = shapeFactory.getShape(Rectangle.class);
        Shape square = shapeFactory.getShape(Square.class);
        Shape circle = shapeFactory.getShape(Circle.class);

        rec.draw();
        square.draw();
        circle.draw();

        AbstractFactory colorFactory = FactoryProducer.getFactory(ColorFactory.class);
        Color blue = colorFactory.getColor(Blue.class);
        Color red = colorFactory.getColor(Red.class);
        Color green = colorFactory.getColor(Green.class);

        blue.fill();
        red.fill();
        green.fill();

        // 单例模式
        BestSingle bestSingle = BestSingle.INSTAENCE;
        BetterLazySingle betterLazySingle = BetterLazySingle.getInstance();
        BetterSingle betterSingle = BetterSingle.getInstance();
        NormalSingle normalSingle = NormalSingle.getInstance();


        // 建造者模式
        Meal.MealBuilder builder = new Meal.MealBuilder();
        builder.prepareNonVerMeal().showItems();
        builder.prepareVerMeal().showItems();


        /**
         *  原型模式：implement Cloneable
         *  浅拷贝：只拷贝值，引用依然指向原来的对象
         *  深拷贝：所有数据都拷贝一份
         */




    }
}

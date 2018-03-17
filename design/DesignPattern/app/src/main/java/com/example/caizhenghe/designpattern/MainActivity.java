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
import com.example.caizhenghe.designpattern.chain.AbstractLogger;
import com.example.caizhenghe.designpattern.chain.ConsoleLogger;
import com.example.caizhenghe.designpattern.chain.ErrorLogger;
import com.example.caizhenghe.designpattern.chain.FileLogger;
import com.example.caizhenghe.designpattern.decorator.RedShapeShapeDecorator;
import com.example.caizhenghe.designpattern.decorator.ShapeDecorator;
import com.example.caizhenghe.designpattern.factory.Circle;
import com.example.caizhenghe.designpattern.factory.Rectangle;
import com.example.caizhenghe.designpattern.factory.Shape;
import com.example.caizhenghe.designpattern.factory.ShapeFactory;
import com.example.caizhenghe.designpattern.factory.Square;
import com.example.caizhenghe.designpattern.memento.CareTaker;
import com.example.caizhenghe.designpattern.memento.Originator;
import com.example.caizhenghe.designpattern.observe.FirstObserver;
import com.example.caizhenghe.designpattern.observe.SecondObserver;
import com.example.caizhenghe.designpattern.observe.Subject;
import com.example.caizhenghe.designpattern.observe.ThirdObserver;
import com.example.caizhenghe.designpattern.single.BestSingle;
import com.example.caizhenghe.designpattern.single.BetterLazySingle;
import com.example.caizhenghe.designpattern.single.BetterSingle;
import com.example.caizhenghe.designpattern.single.NormalSingle;
import com.example.caizhenghe.designpattern.state.Context;
import com.example.caizhenghe.designpattern.state.FirstState;
import com.example.caizhenghe.designpattern.state.SecondState;
import com.example.caizhenghe.designpattern.template.Cricket;
import com.example.caizhenghe.designpattern.template.FootBall;
import com.example.caizhenghe.designpattern.template.Game;
import com.example.caizhenghe.designpattern.visitor.Computer;
import com.example.caizhenghe.designpattern.visitor.ComputerPartDisplayVisitor;
import com.example.caizhenghe.designpattern.visitor.ComputerPartVisitor;

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

        // 装饰者模式
        ShapeDecorator recDecorator = new RedShapeShapeDecorator(rec);
        recDecorator.draw();

        ShapeDecorator circleDecorator = new RedShapeShapeDecorator(circle);
        circleDecorator.draw();

        ShapeDecorator squareDecorator = new RedShapeShapeDecorator(square);
        squareDecorator.draw();

        // 责任链模式
        AbstractLogger loggerChain = getLoggerChain();
        loggerChain.logMessage(AbstractLogger.INFO, "this is console log");
        loggerChain.logMessage(AbstractLogger.DEBUG, "this is debug log");
        loggerChain.logMessage(AbstractLogger.ERROR, "this is error log");

        // 备忘录模式
        Originator originator = new Originator();
        CareTaker careTaker = new CareTaker();
        originator.setState("State #1");
        originator.setState("State #2");
        careTaker.add(originator.saveStateToMemento());
        originator.setState("State #3");
        careTaker.add(originator.saveStateToMemento());
        originator.setState("State #4");

        System.out.println("Current State: " + originator.getState());
        originator.getStateFromMemento(careTaker.get(0));
        System.out.println("First saved State: " + originator.getState());
        originator.getStateFromMemento(careTaker.get(1));
        System.out.println("SecondObserver saved State: " + originator.getState());

        // 观察者模式
        Subject subject = new Subject();
        FirstObserver observer1 = new FirstObserver(subject);
        SecondObserver observer2 = new SecondObserver(subject);
        ThirdObserver observer3 = new ThirdObserver(subject);

        subject.setState(1);
        subject.setState(2);
        subject.setState(3);

        // 状态模式
        Context context = new Context();
        FirstState first = new FirstState();
        SecondState second = new SecondState();
        first.doAction(context);
        Log.d("TAG", context.getState().toString());
        second.doAction(context);
        Log.d("TAG", context.getState().toString());

        // 模板模式
        Game footBall = new FootBall();
        footBall.play();
        Game cricket = new Cricket();
        cricket.play();

        // 访问者模式
        Computer computer = new Computer();
        ComputerPartVisitor visitor = new ComputerPartDisplayVisitor();
        computer.accept(visitor);
    }

    private AbstractLogger getLoggerChain(){
        AbstractLogger errorLogger = new ErrorLogger(AbstractLogger.ERROR);
        AbstractLogger fileLogger = new FileLogger(AbstractLogger.DEBUG);
        AbstractLogger consoleLogger = new ConsoleLogger(AbstractLogger.INFO);
        errorLogger.setNextLogger(fileLogger);
        fileLogger.setNextLogger(consoleLogger);

        return errorLogger;
    }
}

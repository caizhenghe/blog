package com.example.caizhenghe.designpattern.chain;

/**
 * Created by caizhenghe on 2018/3/16.
 */

public abstract class   AbstractLogger {
    public final static int INFO = 1;
    public final static int DEBUG = 2;
    public final static int ERROR = 3;

    protected int level;
    protected AbstractLogger nextLogger;

    public void logMessage(int level, String message){
        if (this.level <= level) {
            write(message);
        }

        if(nextLogger != null){
            nextLogger.logMessage(level, message);
        }
    }

    public void setNextLogger(AbstractLogger logger){
        this.nextLogger = logger;
    }

    protected abstract void write(String message);
}

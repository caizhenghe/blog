package com.example.caizhenghe.designpattern.chain;

import android.util.Log;

/**
 * Created by caizhenghe on 2018/3/16.
 */

public class FileLogger extends AbstractLogger {
    public FileLogger(int level){
        this.level = level;
    }
    @Override
    protected void write(String message) {
        Log.d("TAG", "FileLogger:: " + message);
    }
}

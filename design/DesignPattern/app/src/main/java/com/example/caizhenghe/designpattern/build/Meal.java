package com.example.caizhenghe.designpattern.build;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by caizhenghe on 2018/3/10.
 */

public class Meal {

    ArrayList<Item> mItems = new ArrayList<>();
    public void addItem(Item item){
        mItems.add(item);
    }

    public float getCost(){
        float cost = 0.0f;
        for(Item item : mItems){
            cost += item.price();
        }
        return cost;
    }

    public void showItems(){
        for(Item item : mItems){
            Log.d("TAG", "Item = " + item.name() + "; price = " + item.price() + "; pack = " + item.packing().pack());
        }
    }

    public static class MealBuilder{
        public Meal prepareVerMeal(){
            Meal meal = new Meal();
            meal.addItem(new VerBurger());
            meal.addItem(new Cola());
            return meal;
        }

        public Meal prepareNonVerMeal(){
            Meal meal = new Meal();
            meal.addItem(new ChickenBurger());
            meal.addItem(new Pepsi());
            return meal;
        }
    }
}

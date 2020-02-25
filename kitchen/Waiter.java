package com.javarush.task.Restaurant.kitchen;

import com.javarush.task.Restaurant.ConsoleHelper;

import java.util.Observable;
import java.util.Observer;

public class Waiter implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        ConsoleHelper.writeMessage(arg.toString() + " was cooked by " + o.toString());
    }
}

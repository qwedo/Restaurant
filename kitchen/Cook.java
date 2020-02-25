package com.javarush.task.Restaurant.kitchen;


import com.javarush.task.Restaurant.statistic.StatisticManager;
import com.javarush.task.Restaurant.statistic.event.CookedOrderEventDataRow;
import com.javarush.task.Restaurant.statistic.event.EventDataRow;
import com.javarush.task.Restaurant.ConsoleHelper;

import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

public class Cook extends Observable implements Runnable{
    private String name;
    private boolean busy;
    private LinkedBlockingQueue<Order> queue = new LinkedBlockingQueue<>();

    public boolean isBusy() {
        return busy;
    }

    public Cook(String name) {
        this.name = name;
    }

    public void setQueue(LinkedBlockingQueue<Order> queue) {
        this.queue = queue;
    }

    /*    @Override
    public void update(Observable o, Object arg) {//o - tablet, arg - order
        ConsoleHelper.writeMessage(String.format("Start cooking - %s, cooking time %smin", arg, ((Order) arg).getTotalCookingTime()));
        setChanged();
        notifyObservers(arg);
        EventDataRow event = new CookedOrderEventDataRow(o.toString(), this.name, ((Order) arg).getTotalCookingTime() * 60, ((Order) arg).getDishes());
        StatisticManager.getInstance().register(event);//регистрация события для повара во время приготовления еды
    }*/

    //заменили этим методо update, т.к. поменяли логику повар больше не Observer. Раньше он напрямую следил за планшетом.
    public void startCookingOrder(Order order) {
        busy = true;
        ConsoleHelper.writeMessage(String.format("Start cooking - %s, cooking time %smin", order, order.getTotalCookingTime()));
        try {
            Thread.sleep(order.getTotalCookingTime() * 10);
        } catch (InterruptedException e) {}
        EventDataRow event = new CookedOrderEventDataRow(order.getTablet().toString(), this.name, order.getTotalCookingTime() * 60, order.getDishes());
        StatisticManager.getInstance().register(event);//регистрация события для повара во время приготовления еды
        busy = false;
    }

    @Override
    public void run() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    while (queue.isEmpty())
                        Thread.sleep(10);
                    if (!this.isBusy())
                        //new Thread(() -> {
                            if (!queue.isEmpty())
                                this.startCookingOrder(queue.poll());
                        //}).start();
                }
            } catch (InterruptedException ignored) {
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public String toString() {
        return name;
    }

}

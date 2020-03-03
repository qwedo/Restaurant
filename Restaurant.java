package com.javarush.task.Restaurant;

import com.javarush.task.Restaurant.kitchen.Cook;
import com.javarush.task.Restaurant.kitchen.Order;
import com.javarush.task.Restaurant.kitchen.Waiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Restaurant {
    private static final int ORDER_CREATING_INTERVAL = 100;
    private static final LinkedBlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws InterruptedException {
        Cook cook1 = new Cook("Amigo");
        cook1.setQueue(orderQueue);
        Cook cook2 = new Cook("Ivano");
        cook2.setQueue(orderQueue);
        new Thread(cook1).start();
        new Thread(cook2).start();
        List<Tablet> tablets = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Tablet tablet = new Tablet(i);
            tablet.setQueue(orderQueue);
            tablets.add(tablet);
        }

        Waiter waiter = new Waiter();
        cook1.addObserver(waiter);
        cook2.addObserver(waiter);

        Thread task = new Thread(new RandomOrderGeneratorTask(tablets, ORDER_CREATING_INTERVAL));
        task.start();
        Thread.sleep(1000);
        task.interrupt();

        DirectorTablet directorTablet = new DirectorTablet();
        directorTablet.printAdvertisementProfit();
        directorTablet.printCookWorkloading();
        directorTablet.printActiveVideoSet();
        directorTablet.printArchivedVideoSet();
    }
}

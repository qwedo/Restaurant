package com.javarush.task.Restaurant;


import com.javarush.task.Restaurant.ad.AdvertisementManager;
import com.javarush.task.Restaurant.kitchen.Order;
import com.javarush.task.Restaurant.kitchen.TestOrder;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tablet{
    private final int number;
    private static Logger logger = Logger.getLogger(Tablet.class.getName());//чтобы узнать причину возникновения исключения при работе с консолью(когда наше приложение умрет)
    private LinkedBlockingQueue<Order> queue = new LinkedBlockingQueue<>();

    public Tablet(int number) {
        this.number = number;
    }

    public void setQueue(LinkedBlockingQueue<Order> queue) {
        this.queue = queue;
    }

    public Order createOrder(){ //обрабатываем исключение, которое возникает при создание Order
        Order order = null;
        try {
            order = new Order(this);
            processOrder(order);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Console is unavailable.");
        }
        return order;//возвращаем null, если не удалось создать заказ(возникло исключение в new Order)
    }

    public void createTestOrder(){
        TestOrder testOrder = null;
        try {
            testOrder = new TestOrder(this);
            processOrder(testOrder);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Console is unavailable.");
        }
    }

    private void processOrder(Order order) {
        if (!order.isEmpty()) { //если заказ пустой, то не передаем его повару
            ConsoleHelper.writeMessage(order.toString());
            queue.add(order);
            AdvertisementManager advertisementManager = new AdvertisementManager(order.getTotalCookingTime() * 60);//после того, как создался заказ, запускаем рекламу, которая будет показываться, пока заказ готовится.
            try {
                advertisementManager.processVideos();
            } catch (RuntimeException e) {
                logger.log(Level.INFO, "No video is available for the order " + order);
            }
        }
    }



    @Override
    public String toString() {
        return "Tablet{number=" + number + "}";
    }

}

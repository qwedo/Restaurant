package com.javarush.task.Restaurant;

import java.util.List;

public class RandomOrderGeneratorTask implements Runnable {
    private List<Tablet> tablets;
    private int orderCreatingInterval;

    public RandomOrderGeneratorTask(List<Tablet> tablets, int interval) {
        this.tablets = tablets;
        this.orderCreatingInterval = interval;
    }

    @Override
    public void run() {
        while (true) {
            Tablet tablet = tablets.get((int) (Math.random() * (tablets.size() - 1)));//выбираем случайный планшет
            tablet.createTestOrder();
            try {
                Thread.sleep(orderCreatingInterval);
            } catch (InterruptedException ignored) {}
        }
    }
}

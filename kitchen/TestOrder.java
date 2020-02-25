package com.javarush.task.Restaurant.kitchen;

import com.javarush.task.Restaurant.Tablet;

import java.io.IOException;
import java.util.Random;

public class TestOrder extends Order {

    public TestOrder(Tablet tablet) throws IOException {
        super(tablet);
    }

    //инициализация списка блюд для заказа рандомными значениями
    @Override
    protected void initDishes(){
/*        dishes = new ArrayList<>(Arrays.asList(Dish.values()))
                .stream()
                .filter((d) -> new Random().nextDouble() > 0.5)
                .collect(Collectors.toList());*/

        for (int i = 0; i < Dish.values().length; i++) {
            dishes.add(Dish.values()[new Random().nextInt(Dish.values().length - 1) + 1]);
        }
    }
}

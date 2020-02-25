package com.javarush.task.Restaurant.kitchen;

import com.javarush.task.Restaurant.ConsoleHelper;
import com.javarush.task.Restaurant.Tablet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final Tablet tablet;
    protected List<Dish> dishes = new ArrayList<>();


    public Order(Tablet tablet) throws IOException {//возникает исключение от создания поля dishes при помощи getAllDishesForOrder()
        this.tablet = tablet;
        initDishes();
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public int getTotalCookingTime(){
        return dishes.stream().mapToInt(Dish::getDuration).sum();
    }

    protected void initDishes() throws IOException {
        dishes = ConsoleHelper.getAllDishesForOrder();
    }

    public Tablet getTablet() {
        return tablet;
    }

    public boolean isEmpty(){
        return getTotalCookingTime() == 0;
    }

    @Override
    public String toString() {
        return dishes.isEmpty() ? "" : String.format("Your order: %s of %s", dishes, tablet);
    }
}

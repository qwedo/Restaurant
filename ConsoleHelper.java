package com.javarush.task.Restaurant;

import com.javarush.task.Restaurant.kitchen.Dish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ConsoleHelper {
    public static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message){
        System.out.println(message);
    }

    public static String readString() throws IOException {
        return bufferedReader.readLine();
    }

    public static List<Dish> getAllDishesForOrder() throws IOException {
        List<Dish> result = new ArrayList<>();
        writeMessage("Выберите блюда из меню ниже. Для выхода введите exit.");
        writeMessage(Dish.allDishesToString());
        String line;
        boolean enumContain = false;

        while (!(line = readString()).equals("exit")){//тут может возникнуть исключение
            for (Dish dish : Dish.values()){
                if (dish.name().equals(line)) {
                    enumContain = true;
                    result.add(dish);
                    break;
                }
            }
            if (!enumContain)
                writeMessage("Такого блюда нет.");
        }
        return result;
    }
}

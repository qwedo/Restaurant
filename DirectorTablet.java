package com.javarush.task.Restaurant;

import com.javarush.task.Restaurant.ad.Advertisement;
import com.javarush.task.Restaurant.ad.StatisticAdvertisementManager;
import com.javarush.task.Restaurant.statistic.StatisticManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DirectorTablet {

    public void printAdvertisementProfit(){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
        Map<LocalDate, Double> resultMap = StatisticManager.getInstance().getAdData();
        if (resultMap == null || resultMap.isEmpty()) return;
        Double totalProfit = 0d;//сумма профитов за каждый день
        for (Map.Entry<LocalDate, Double> entry : resultMap.entrySet())
            totalProfit = totalProfit + entry.getValue();

        resultMap.forEach((date, dayProfit) -> ConsoleHelper.writeMessage(String.format(Locale.ENGLISH,"%s - %.2f", dateFormat.format(date), dayProfit)));
        if (totalProfit > 0) {
            ConsoleHelper.writeMessage(String.format(Locale.ENGLISH, "Total - %.2f", totalProfit));
        }
    }

    public void printCookWorkloading(){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
        Map<LocalDate, Map<String, Integer>> resultMap = StatisticManager.getInstance().getCookWorkLoadingData();
        if (resultMap == null || resultMap.isEmpty()) return;
        for (Map.Entry<LocalDate, Map<String, Integer>> entry : resultMap.entrySet()){
            ConsoleHelper.writeMessage(dateFormat.format(entry.getKey()));
            entry.getValue().forEach((cookName, cookTime) -> ConsoleHelper.writeMessage(String.format("%s - %s min", cookName, cookTime)));
            ConsoleHelper.writeMessage("");
        }
    }

    public void printActiveVideoSet(){
        List<Advertisement> adList = StatisticAdvertisementManager.getInstance().getActiveVideoSet();
        adList.sort((ad1, ad2) -> ad1.getName().compareToIgnoreCase(ad2.getName()));
        adList.forEach((ad) -> ConsoleHelper.writeMessage(String.format("%s - %d", ad.getName(), ad.getHits())));
    }

    public void printArchivedVideoSet(){
        List<Advertisement> adList = StatisticAdvertisementManager.getInstance().getArchivedVideoSet();
        adList.sort((ad1, ad2) -> ad1.getName().compareToIgnoreCase(ad2.getName()));
        adList.forEach((ad) -> ConsoleHelper.writeMessage(ad.getName()));
    }
}

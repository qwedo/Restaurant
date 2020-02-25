package com.javarush.task.Restaurant.statistic;

import com.javarush.task.Restaurant.statistic.event.CookedOrderEventDataRow;
import com.javarush.task.Restaurant.statistic.event.EventDataRow;
import com.javarush.task.Restaurant.statistic.event.EventType;
import com.javarush.task.Restaurant.statistic.event.VideoSelectedEventDataRow;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class StatisticManager {
    private static StatisticManager instance;
    private StatisticStorage statisticStorage = new StatisticStorage();
    //private Set<Cook> cooks = new HashSet<>();

    private StatisticManager(){}

    /*public Set<Cook> getCooks() {
        return cooks;
    }*/

    public static StatisticManager getInstance(){
        if (instance == null){
            instance = new StatisticManager();
            return instance;
        }
        else
            return instance;
    }

    public void register(EventDataRow data){
        statisticStorage.put(data);
    }

    /*public void register(Cook cook){
        cooks.add(cook);
    }*/

    public Map<LocalDate, Map<String, Integer>> getCookWorkLoadingData(){
        Map<LocalDate, Map<String, Integer>> cookMap = new TreeMap<>(Collections.reverseOrder());
        List<CookedOrderEventDataRow> eventList = castEventListToCookList(statisticStorage.getStorage(EventType.COOKED_ORDER));
        for (CookedOrderEventDataRow event : eventList)
            updateCookMap(cookMap, event);
        return cookMap;
    }

    public Map<LocalDate, Double> getAdData(){//достает все данные из хранилища, относящиеся к отображению рекламы, и считает общую прибыль за каждый день.
        Map<LocalDate, Double> resultMap = new TreeMap<>(Collections.reverseOrder());//в конструктор передали компаратор, т.к. нужно в убывающем порядке
        List<VideoSelectedEventDataRow> eventList = castEventListToAdList(statisticStorage.getStorage(EventType.SELECTED_VIDEOS));//т.е. тип листа изначально EventDataRow
        for (VideoSelectedEventDataRow event : eventList)
            updateAdMap(resultMap, event);
        return resultMap;
    }

    private void updateCookMap(Map<LocalDate, Map<String, Integer>> resultMap, CookedOrderEventDataRow event){
        LocalDate cookDate = convertToLocalDate(event.getDate());
        String cookName = event.getCookName();
        Integer cookTime = event.getTime() % 60 == 0 ? event.getTime() / 60 : event.getTime() / 60 + 1;//округляем в большую сторону(по условию) Хотя по идее это не должно получиться
        if (!resultMap.containsKey(cookDate)){//если нет данных для этого дня
            Map<String, Integer> map = new TreeMap<>();
            map.put(cookName, cookTime);
            resultMap.put(cookDate, map);
        }
        else if (!resultMap.get(cookDate).containsKey(event.getCookName())){//если запись для дня есть(есть мапа), но нет данных для этого повара
            resultMap.get(cookDate).put(cookName,cookTime);
        }
        else {//есть и запись для дня и запись для повара. Просто прибавляем время для этого повара
            Integer oldCookTime = resultMap.get(cookDate).get(cookName);
            resultMap.get(cookDate).put(cookName, oldCookTime + cookTime);
        }

    }

    private void updateAdMap(Map<LocalDate, Double> resultMap, VideoSelectedEventDataRow event){//обновление данных в итоговой мапе
        LocalDate localDate = convertToLocalDate(event.getDate());
        Double eventProfit = event.getAmount() / 100.00;//заработок с показа рекламы в рублях, а не копейках
        if (resultMap.containsKey(localDate)){
            Double dayProfit = resultMap.get(localDate);//то, что заработали за день
            resultMap.put(localDate, dayProfit + eventProfit);
        }
        else
            resultMap.put(localDate, eventProfit);
    }

    private List<CookedOrderEventDataRow> castEventListToCookList(List<EventDataRow> list){
        List<CookedOrderEventDataRow> eventList = new ArrayList<>();
        for (EventDataRow originEvent : list)//прошлись по каждому элементу, добавили в новый лист и закастили его
            eventList.add((CookedOrderEventDataRow) originEvent);
        return eventList;
    }

    private List<VideoSelectedEventDataRow> castEventListToAdList(List<EventDataRow> list){
        List<VideoSelectedEventDataRow> eventList = new ArrayList<>();
        for (EventDataRow originEvent : list)//прошлись по каждому элементу, добавили в новый лист и закастили его
            eventList.add((VideoSelectedEventDataRow) originEvent);
        return eventList;
    }

    private LocalDate convertToLocalDate(Date dateToConvert) {//конвертируем, т.к. LocalDate хранит дату просто - год, месяц, день
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }



    private class StatisticStorage{//создали внутренний класс, т.к. StatisticManager имеет доступ только к одному StatisticStorage
       private Map<EventType, List<EventDataRow>> storage = new HashMap<>();

        public StatisticStorage() {
            for (EventType eventType : EventType.values())
                storage.put(eventType, new ArrayList<>());
        }

        private void put(EventDataRow data){//модификатор private делает так, что к методу нельзя получить доступ за пределами класса StatisticManager. Особенности вложенных классов.
            storage.get(data.getType()).add(data);//нашли в мапе тип события и в ArrayList добавили это событие
        }

        public List<EventDataRow> getStorage(EventType eventType) {
            return storage.get(eventType);
        }
    }
}

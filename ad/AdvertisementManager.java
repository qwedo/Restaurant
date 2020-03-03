package com.javarush.task.Restaurant.ad;

import com.javarush.task.Restaurant.statistic.StatisticManager;
import com.javarush.task.Restaurant.ConsoleHelper;
import com.javarush.task.Restaurant.statistic.event.VideoSelectedEventDataRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdvertisementManager {
    private final AdvertisementStorage storage = AdvertisementStorage.getInstance();

    private List<Advertisement> bestListSet = null;
    private int timeSeconds;//время выполнения заказа поваром(в которое должны уложиться все наши ролики)
    private long bestPrice = 0;

    public AdvertisementManager(int timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    public void processVideos(){
        if (storage.list().size() == 0){ //Если нет рекламных видео, которые можно показать посетителю
            throw new NoVideoAvailableException();
        }
        else {
            List<Advertisement> adList = getAdsWithHits(storage.list());
            MakeAllSets(adList);
            sortAds(bestListSet);
            EventDataRow event = new VideoSelectedEventDataRow(bestListSet, bestPrice, CalcTime(bestListSet));
            StatisticManager.getInstance().register(event);//регистрация события "видео выбрано" перед отображением рекламы пользователю
            showAds(bestListSet);
        }
    }

    //сортировка в порядке уменьшения стоимости показа одного рекламного ролика в копейках
    //после сортировка по увеличению стоимости показа одной секунды рекламного ролика в тысячных частях копейки
    private void sortAds(List<Advertisement> ads){
        ads.sort(Comparator.comparingLong(Advertisement::getAmountPerOneDisplaying)
                 .reversed()
                 .thenComparing(ad -> ad.getAmountPerOneDisplaying() * 1000 / ad.getDuration()));
    }

    private void showAds(List<Advertisement> ads){//Отобразить все рекламные ролики. для каждого показа сделать ревалидейт!
        for (Advertisement ad : ads) {
            ConsoleHelper.writeMessage(String.format("%s is displaying... %d, %d", ad.getName(), ad.getAmountPerOneDisplaying(), ad.getAmountPerOneDisplaying() * 1000 / ad.getDuration()));
            ad.revalidate();
        }
    }

    //выбираем только те рекламные ролики, в которых количество показов(hits) > 0. Чтобы не создавать дополнительный геттер, сделал это через getAmountPerOneDisplaying
    private List<Advertisement> getAdsWithHits (List<Advertisement> ads){
        return ads.stream().filter((ad) -> ad.getAmountPerOneDisplaying() > 0).collect(Collectors.toList());
    }

    private void MakeAllSets (List<Advertisement> ads){//создание всех наборов перестановок и их проверка. По итогу отработки метода в bestListOrder будет наилучший набор.
        if (ads.size() > 0)
            CheckList(ads);

        if (CalcTime(ads) > timeSeconds) {//Иначе зачем, ведь все ролики помещаются, а цена будет становится только меньше. Но изначально этого пункта по ссылке vscode не было
            for (int i = 0; i < ads.size(); i++) {
                List<Advertisement> newAds = new ArrayList<>(ads);
                newAds.remove(i);
                MakeAllSets(newAds);
            }
        }
    }

    private void CheckList (List<Advertisement> ads){//проверяем, является ли список лучшим
        if (CalcTime(ads) <= timeSeconds) { //если продолжительность набора роликов меньше времени готовки блюд, то проавливаемся во внутрь. Иначе этот набор нам не подходит.
            if (CalcPrice(ads) > bestPrice) {
                bestListSet = ads;
                bestPrice = CalcPrice(ads);
            }
            else if (CalcPrice(ads) == bestPrice) { //если цена за ролики равна
                if (CalcTime(ads) > CalcTime(bestListSet))//выбираем набор роликов с максимальной продолжительностю
                    bestListSet = ads;
                else if (CalcTime(ads) == CalcTime(bestListSet)) {//если цена и продолжительность набора роликов равна
                    if (ads.size() < bestListSet.size()) //то выбираем вариант с минимальным количеством роликов в наборе
                        bestListSet = ads;
                }
            }
        }
    }

    private long CalcPrice (List<Advertisement> ads){
        return ads.stream().mapToLong(Advertisement::getAmountPerOneDisplaying).sum();
    }

    private int CalcTime (List<Advertisement> ads){
        return ads.stream().mapToInt(Advertisement::getDuration).sum();
    }
}

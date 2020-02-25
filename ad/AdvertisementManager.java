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



/*    public void processVideos(){
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

    private void sortAds(List<Advertisement> ads){
        ads.sort(Comparator.comparingLong(Advertisement::getAmountPerOneDisplaying).reversed().thenComparing(ad -> ad.getAmountPerOneDisplaying() * 1000 / ad.getDuration()));//сортировка в порядке уменьшения стоимости показа одного рекламного ролика в копейках
        //после сортировка по увеличению стоимости показа одной секунды рекламного ролика в тысячных частях копейки
    }

    private void showAds(List<Advertisement> ads){//Отобразить все рекламные ролики. для каждого показа сделать ревалидейт!
        for (Advertisement ad : ads) {
            ConsoleHelper.writeMessage(String.format("%s is displaying... %d, %d", ad.getName(), ad.getAmountPerOneDisplaying(), ad.getAmountPerOneDisplaying() * 1000 / ad.getDuration()));
            ad.revalidate();
        }
    }

    private List<Advertisement> getAdsWithHits (List<Advertisement> ads){ //выбираем только те рекламные ролики, в которых количество показов(hits) > 0. Чтобы не создавать дополнительный геттер, сделал это через getAmountPerOneDisplaying
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
    }*/


    //скопировал метод, т.к. мой не проходил в 16 задаче
    public void processVideos() throws NoVideoAvailableException {

        List<Advertisement> videos = storage.list();
        if (storage.list().isEmpty())
            throw new NoVideoAvailableException();

        // ищем список видео для показа согласно критериям
        List<Advertisement> bestAds = new VideoHelper().findAllYouNeed();

        // сортируем полученный список
        Collections.sort(bestAds, new Comparator<Advertisement>() {
            @Override
            public int compare(Advertisement video1, Advertisement video2) {
                long dif = video2.getAmountPerOneDisplaying() - video1.getAmountPerOneDisplaying();
                if (dif == 0) dif = video2.getDuration() - video1.getDuration();
                return (int) dif;
            }
        });
        long amount = 0;
        int totalDuration = 0;
        for (Advertisement ad : bestAds) {
            totalDuration += ad.getDuration();
            amount += ad.getAmountPerOneDisplaying();
        }
        StatisticManager.getInstance().register(new VideoSelectedEventDataRow(bestAds, amount, totalDuration));
        //StatisticManager.getInstance().register(new VideoSelectedEventDataRow(videosToShow, totalAmount, totalDuration));

        // выводим список
        for (Advertisement ad : bestAds) {
            ConsoleHelper.writeMessage(ad.getName() + " is displaying... " +
                    ad.getAmountPerOneDisplaying() + ", " +
                    1000 * ad.getAmountPerOneDisplaying() / ad.getDuration());
            ad.revalidate();
        }
    }

    private class VideoHelper {
        private int bestPrice = 0;
        private int maxTime = 0;
        private int numberOfClips = 0;
        private List<Advertisement> bestAds = new ArrayList<>();
        private List<Advertisement> candidates = new ArrayList<>();

        public List<Advertisement> findAllYouNeed() {
            // отбор кандидатов
            for (Advertisement ad : storage.list()) {
                if (ad.getDuration() <= timeSeconds && ad.getHits() > 0)
                    candidates.add(ad);
            }
            if (candidates.isEmpty()) {
                throw new NoVideoAvailableException();
            } else findBestAds(new BinaryPattern(candidates.size()));
            return bestAds;
        }

        // рекурсивная функция формирования списка для показа
        public void findBestAds(BinaryPattern pattern) {
            while (true) {
                checkAds(pattern.getPattern());
                if (!pattern.full()) pattern.increment();
                else break;
                findBestAds(pattern);
            }
        }

        // проверка очередного набора видеоклипов
        private void checkAds(int[] pattern) {
            int price = 0;
            int time = 0;
            List<Advertisement> list = new ArrayList<>();
            for (int i = 0; i < candidates.size(); i++) {
                price += candidates.get(i).getAmountPerOneDisplaying() * pattern[i];
                time += candidates.get(i).getDuration() * pattern[i];
                if (pattern[i] == 1) list.add(candidates.get(i));
            }
            if (time > timeSeconds) return;
            if (!(price > bestPrice)) {
                if (!(price == bestPrice && time > maxTime)) {
                    if (!(price == bestPrice && time == maxTime && list.size() < numberOfClips)) {
                        return;
                    }
                }
            }
            bestAds = list;
            bestPrice = price;
            maxTime = time;
            numberOfClips = list.size();
        }

        // формирование двоичных масок для сбора списка видеоклипов
        private class BinaryPattern {
            private int length;
            private int count;

            public BinaryPattern(int size) {
                this.length = size;
                this.count = 0;
            }

            public int[] getPattern() {
                String regString = Integer.toBinaryString(count);
                int dif = length - regString.length();
                int[] pattern = new int[length];
                for (int j = dif; j < pattern.length; j++) {
                    if (regString.charAt(j - dif) == '1') pattern[j] = 1;
                    else pattern[j] = 0;
                }
                return pattern;
            }

            public void increment() {
                count++;
            }

            ;

            public boolean full() {
                return count == (int) Math.pow(2, length) - 1;
            }
        }
    }
}

package com.javarush.task.Restaurant.ad;

import java.util.List;
import java.util.stream.Collectors;

public class StatisticAdvertisementManager {
    private static StatisticAdvertisementManager instance;
    private AdvertisementStorage adStorage = AdvertisementStorage.getInstance();

    private StatisticAdvertisementManager(){}

    public static StatisticAdvertisementManager getInstance(){
        if (instance == null) instance = new StatisticAdvertisementManager();
        return instance;
    }

    public List<Advertisement> getActiveVideoSet(){
        return adStorage.list().stream().filter((ad) -> ad.getHits() > 0).collect(Collectors.toList());
    }

    public List<Advertisement> getArchivedVideoSet(){
        return adStorage.list().stream().filter((ad) -> ad.getHits() == 0).collect(Collectors.toList());
    }
}

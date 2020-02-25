package com.javarush.task.Restaurant.ad;

import java.util.ArrayList;
import java.util.List;

public class AdvertisementStorage {
    private static AdvertisementStorage instance;
    private final List<Advertisement> videos = new ArrayList<>();

    private AdvertisementStorage() {}

    public static AdvertisementStorage getInstance(){
        if (instance == null){
            instance = new AdvertisementStorage();
            Object someContent = new Object();
            instance.add(new Advertisement(someContent, "First Video", 5000, 100, 3 * 60)); // 3 min
            instance.add(new Advertisement(someContent, "Second Video", 100, 10, 15 * 60)); //15 min
            instance.add(new Advertisement(someContent, "Third Video", 400, 2, 10 * 60)); //10 min
            instance.add(new Advertisement(someContent, "Четвертое видео", 400, 0, 10 * 60));
            return instance;
        }
        else
            return instance;
    }

    public List<Advertisement> list(){
        return videos;
    }

    public void add(Advertisement advertisement){
        videos.add(advertisement);
    }
}

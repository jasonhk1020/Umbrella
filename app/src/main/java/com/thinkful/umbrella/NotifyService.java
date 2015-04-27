package com.thinkful.umbrella;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NotifyService extends IntentService {

    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    public NotifyService() {
        super("NotifyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("orders","onHandleIntent");
        WeatherService weatherService = new WeatherService();
        String weatherResponse = weatherService.getWeather(intent.getStringExtra(LATITUDE),intent.getStringExtra(LONGITUDE));
        Notifier notification = new Notifier();
        Log.i("weatherResponse",weatherResponse);
        switch (weatherResponse){
            case "Yes":
                Log.i("case_weatherResponse","Yes");
                notification.createNotification(this,"Yes");
            case "No":
                Log.i("case_weatherResponse","No");
                notification.createNotification(this,"No");
        }
        AlarmBroadcastReceiver.completeWakefulIntent(intent);
    }

}
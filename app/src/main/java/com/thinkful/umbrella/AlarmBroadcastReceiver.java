package com.thinkful.umbrella;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by jasonhk1020 on 4/27/2015.
 */
public class AlarmBroadcastReceiver extends WakefulBroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notifyService = new Intent(context, NotifyService.class);
        notifyService.putExtra(NotifyService.LATITUDE, intent.getStringExtra(NotifyService.LATITUDE));
        Log.i("orders","lat");
        notifyService.putExtra(NotifyService.LONGITUDE, intent.getStringExtra(NotifyService.LONGITUDE));
        Log.i("orders","long");
        startWakefulService(context, notifyService);
    }
}

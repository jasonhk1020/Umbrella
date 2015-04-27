package com.thinkful.umbrella;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by jasonhk1020 on 4/27/2015.
 */
public class Notifier {
    private int notificationID = 100;

    public void createNotification(Context context,String umbrellaStr) {


        //Build your notification
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                context);
        nBuilder.setContentTitle("Notification");
        Log.i("Notifier",umbrellaStr);
        nBuilder.setContentText("Take an umbrella? " + umbrellaStr);
        nBuilder.setSmallIcon(R.mipmap.ic_launcher);
        nBuilder.setAutoCancel(true);

        //Add a notification action
        nBuilder.setContentIntent(getMainActivityPendingIntent(context));

        //post notification
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, nBuilder.build());
    }
    protected PendingIntent getMainActivityPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return(pendingIntent);
    }
}

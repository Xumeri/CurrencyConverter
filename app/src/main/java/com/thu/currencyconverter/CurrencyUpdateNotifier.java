package com.thu.currencyconverter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class CurrencyUpdateNotifier {

    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "currencyUpdate_channel";
    private static final String CHANNEL_DESCRIPTION = "Show currency update state";
    private final NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;

    public CurrencyUpdateNotifier(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
        if (notificationChannel == null) {
            notificationChannel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_DESCRIPTION, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.arrow_down_float)
                .setContentTitle("Currency Update")
                .setAutoCancel(true);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(resultPendingIntent);
    }

    public void showOrUpdateNotification(int count) {
        notificationBuilder.setContentText("Successfully updated " + count + " currencies");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}

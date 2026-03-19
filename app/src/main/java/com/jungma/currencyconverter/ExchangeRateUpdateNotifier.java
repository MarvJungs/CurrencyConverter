package com.jungma.currencyconverter;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ExchangeRateUpdateNotifier {
    private static final int NOTIFICATION_ID = 123;

    private static String CHANNEL_ID = "currencyconverter_channel";
    private static String CHANNEL_DESCRIPTION = "Notify on Update Finish";

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    public ExchangeRateUpdateNotifier(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_DESCRIPTION, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("CurrencyConverter")
                .setContentText("Placeholder Text")
                .setSmallIcon(R.drawable.ic_stat_attach_money)
                .setAutoCancel(false);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pendingIntent);
    }

    public void showAndUpdateNotification(String text) {
        Log.d("Motification", "Notification sent!");
        notificationBuilder.setContentText(text);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}

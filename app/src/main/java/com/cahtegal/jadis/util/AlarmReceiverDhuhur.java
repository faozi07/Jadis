package com.cahtegal.jadis.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.cahtegal.jadis.R;
import com.cahtegal.jadis.activity.MenuUtama;

/*
 * Created by faozi on 19/02/18.
 */
public class AlarmReceiverDhuhur extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        sendNotification(context);
    }
    @SuppressWarnings("deprecation")
    private void sendNotification(Context context) {
        Intent intent = new Intent(context, MenuUtama.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Jangan lupa Shalat Dhuhur :)")
                .setContentText("Ayo Shalat Berjama'ah")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(1 /* ID of notification */,
                    notificationBuilder.build());
        }
    }

}

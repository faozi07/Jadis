package com.cahtegal.jadis.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cahtegal.jadis.R;
import com.cahtegal.jadis.activity.MenuUtama;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/***
 * Created by root on 31/05/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    public static String notif="";
    BroadcastReceiver broadcastReceiver;
    String title = "", bodyMessage = "", image = "";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        notif = remoteMessage.getNotification().getBody();
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notif: " + notif);
        if (!notif.equals("")) {
            handleNotification(notif);
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("pushNotification")) {
                    bodyMessage = intent.getStringExtra("message");
                    title = intent.getStringExtra("title");
                }
            }
        };
        try {
            JSONObject message = new JSONObject();
            Set<String> keys = remoteMessage.getData().keySet();
            for (String key : keys) {
                try {
                    message.put(key, remoteMessage.getData().get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                /*title = message.getString("title");
                bodyMessage = message.getString("message");*/
                image = message.getString("image");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
        sendNotification(title, bodyMessage, image);
        notif = "";
    }

    private void handleNotification(String body) {
        Intent pushNotification = new Intent("pushNotification");
        pushNotification.putExtra("message",body);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }


    private void sendNotification(String title, String bodyMessage, String image) {

        Intent goPage = new Intent(this, MenuUtama.class);


        goPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, goPage,
                PendingIntent.FLAG_ONE_SHOT);


        //Setung notification sound
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Getting bigPicture
        Bitmap remote_picture = null;
        NotificationCompat.BigPictureStyle notificationBuilderPicture;

        if(!image.equals("")){
            notificationBuilderPicture = new
                    NotificationCompat.BigPictureStyle();
            try {
                remote_picture = BitmapFactory.decodeStream(
                        (InputStream) new URL(image).getContent());

            } catch (IOException e) {
                e.printStackTrace();
            }
            notificationBuilderPicture.bigPicture(remote_picture).setSummaryText(bodyMessage);
        } else {
            notificationBuilderPicture = null;
        }

        //Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setColor(Color.parseColor("#000000"))
                .setContentTitle("Jangan lupa") //top title
                .setContentText("Cek jadwal majelis minggu ini sekarang") //message-text from firebaseconsole
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setStyle(notificationBuilderPicture)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(1, notificationBuilder.build());
        }

    }
}
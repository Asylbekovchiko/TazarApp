package com.io.tazarapp.utils.gsm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.io.tazarapp.R;
import com.io.tazarapp.ui.auth.login.LoginActivity;
import com.io.tazarapp.utils.LanguagePref;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // If the application is in the foreground handle both data and drugstore messages here.
        // Also if you intend on generating your own drugstore as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        Log.e("ACTION", data.toString() + "as");
        sendNotification(notification, data);
    }
    /**
     * Create and show a custom drugstore containing the received FCM message.
     *
     * @param notification FCM drugstore payload received.
     * @param data         FCM data payload received.
     */
    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Intent intent = new Intent(this, LoginActivity.class);
//{'id': 203, 'title_ru': 'New ru', 'title_en': None, 'title_kg': None, 'image': None}

        intent.putExtra("push_id",data.get("id"));
        LanguagePref.LanguageModule lanModule = new LanguagePref.LanguageModule();
        String lan = lanModule.getLanguage(getApplicationContext());
        Log.e("lan", lan);
        String title;
        switch (lan) {
            case "ky": title = data.get("title_kg"); break;
            case "en": title = data.get("title_en"); break;
            case "ru": title = data.get("title_ru"); break;
            default: title = data.get("title");
        }
        if (title.equals("")) title = data.get("title");


//        if (action.equals("product")) {
//            String manufacture = data.get("manufacture");
//            String id = data.get("product");
//            String category = data.get("category");
//            intent.putExtra("id", id);
//            intent.putExtra("manufacture_id",manufacture);
//            intent.putExtra("category_id",category);
//
//        } else {
//            String id = data.get("id");
//            intent.putExtra("id", id);
//        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.parseInt(Objects.requireNonNull(data.get("id"))), intent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setLargeIcon(icon)
                .setColor(Color.GREEN)
                .setLights(Color.GREEN, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_logo_app);

        try {
            String picture_url = data.get("image");
            if (picture_url != null && !"".equals(picture_url)) {
                URL url = new URL(picture_url);
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bigPicture)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("channel description");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setSound(alarmSound, attributes);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(channel);
        }

        if (data.get("id") != null) {
            notificationManager.notify(Integer.parseInt(Objects.requireNonNull(data.get("id"))), notificationBuilder.build());
        } else {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}

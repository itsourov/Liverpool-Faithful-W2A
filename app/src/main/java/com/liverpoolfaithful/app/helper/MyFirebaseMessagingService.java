package com.liverpoolfaithful.app.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.liverpoolfaithful.app.MainActivity;
import com.liverpoolfaithful.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("postID",remoteMessage.getData().get("postID"));
        intent.putExtra("title",remoteMessage.getData().get("title"));
        intent.putExtra("imageLink",remoteMessage.getData().get("imageLink"));
        intent.putExtra("selfUrl",remoteMessage.getData().get("selfUrl"));
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap defaultLogo = BitmapFactory.decodeResource(this.getResources(), R.drawable.play_store_512);

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setLargeIcon(getBitmapFromURL(remoteMessage.getData().get("imageLink"), defaultLogo))
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(getBitmapFromURL(remoteMessage.getData().get("imageLink"), defaultLogo))
                                .bigLargeIcon(null))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);



        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Post Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Random r = new Random();
        int i1 = r.nextInt(45 - 28) + 28;
        notificationManager.notify(i1 /* ID of notification */, notificationBuilder.build());

    }
    public static Bitmap getBitmapFromURL(String src,Bitmap defaultLogo) {

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            Log.d("TAG", "getBitmapFromURL: error  "+src+e.getCause()) ;
            return defaultLogo;
        }
    }

}

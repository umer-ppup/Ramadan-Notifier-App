package com.atrule.ramadannotifier.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.atrule.ramadannotifier.R;
import com.atrule.ramadannotifier.activities.RamadanActivity;
import com.atrule.ramadannotifier.classes.NotificationID;

public class AlarmService extends JobIntentService {
    //region variable declarations
    private MediaPlayer mMediaPlayer;
    private Button stopAlarm;
    private TextView tvLabel;
    private Context context;
    String message;

    private static final int JOB_ID = 2;
    //endregion
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, AlarmService.class, JOB_ID, intent);
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //region code to handle notification alarm
        if (intent.getExtras().getString("alarm").equals("Sehri")){
            sendNotification("Wake up... it is Sehri Time...", "Sehri Notification");
        } else if (intent.getExtras().getString("alarm").equals("Aftar")){
            sendNotification("Its time to open Roza...", "Iftar Notification");
        }
        //endregion
    }

    //region notification generate function
    private void sendNotification(String message, String title) {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.azan);
        Intent intent = new Intent(this, RamadanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyChannel")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSound(uri)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyChannel";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MyChannel", name, importance);
            channel.setDescription(description);
            channel.setSound(uri, new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build());
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NotificationID.getID(), builder.build());
    }
    //endregion
}

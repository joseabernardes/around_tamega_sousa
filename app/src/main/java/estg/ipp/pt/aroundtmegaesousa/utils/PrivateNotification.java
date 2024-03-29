package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.services.UploadFirebaseService;

/**
 * Created by PC on 13/01/2018.
 */

public class PrivateNotification {

    public static final String DEFAULT_CHANNEL = "around.t.s";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id;
    private Context context;


    /**
     * Constructor for progress notification
     *
     * @param context
     * @param title
     * @param icon
     * @param id
     */

    public PrivateNotification(Context context, String title, int icon, int id) {
        this.context = context;
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(DEFAULT_CHANNEL, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
        this.mBuilder = new NotificationCompat.Builder(context, DEFAULT_CHANNEL);
        mBuilder.setContentTitle(title)
                .setSmallIcon(icon);
        this.id = id;
    }


    /**
     * Constructor for regular notification
     *
     * @param context
     * @param title
     * @param content
     * @param icon
     * @param id
     */
    public PrivateNotification(Context context, String title, String content, int icon, int id) {
        this.context = context;
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new NotificationCompat.Builder(context, DEFAULT_CHANNEL);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(DEFAULT_CHANNEL, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
                .setLights(Color.GREEN, 3000, 3000);

        this.id = id;
        setValues();
    }


    public void setValues() {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notif = m.getBoolean("sound", false);
        boolean vibration = m.getBoolean("vibration", false);
        if (vibration) {
            mBuilder.setVibrate(new long[]{0, 1000});
        }
        if (notif) {
            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        }

    }

    /**
     * Show the notification
     */
    public void show() {
        mNotifyManager.notify(id, mBuilder.build());
    }

    /**
     * Show de notification with the given progress
     *
     * @param progress
     */
    public void updateStatus(double progress) {
        mBuilder.setProgress(100, (int) progress, false);
        show();
    }

    /**
     * Cancel the notification
     */
    public void cancel() {
        mNotifyManager.cancel(id);
    }


    /**
     * Sets a pending intent to be trigged on notification click
     *
     * @param pendingIntent
     */
    public void setAction(PendingIntent pendingIntent) {
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        show();
    }

    /**
     * Get the notification ID
     *
     * @return
     */
    public int getId() {
        return id;
    }


    public static int getRandomID() {
        return (int) ((System.nanoTime() / 1000L) % Integer.MAX_VALUE);
    }

    public static void cancelByID(Context context, int id) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);
    }
}

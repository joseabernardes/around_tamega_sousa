package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.services.UploadFirebaseService;

/**
 * Created by PC on 13/01/2018.
 */

public class AppNotification {

    public static final String DEFAULT_CHANNEL = "around.t.s";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id;

    /**
     * Constructor for progress notification
     *
     * @param context
     * @param title
     * @param icon
     * @param id
     */
    public AppNotification(Context context, String title, int icon, int id) {
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new NotificationCompat.Builder(context, DEFAULT_CHANNEL);
        mBuilder.setContentTitle(title)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setOngoing(true);
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
    public AppNotification(Context context, String title, String content, int icon, int id) {
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new NotificationCompat.Builder(context, DEFAULT_CHANNEL);
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
        this.id = id;
    }

    /**
     * Show the notification
     */
    public void show() {
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void updateStatus(double progress) {
        mBuilder.setProgress(100, (int) progress, false);
        show();
    }

    public void cancelNotification(int idCancel) {
        mNotifyManager.cancel(idCancel);
    }

    public void setAction(PendingIntent pendingIntent) {
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        show();
    }


}

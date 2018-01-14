package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import estg.ipp.pt.aroundtmegaesousa.R;

/**
 * Created by PC on 13/01/2018.
 */

public class AppNotification {

    public static final String DEFAULT_CHANNEL = "around.t.s";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id;
    private int current;

    public AppNotification(Context context, String title, int icon, int id) {
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new NotificationCompat.Builder(context, DEFAULT_CHANNEL);
        mBuilder.setContentTitle(title)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));

        this.id = id;
        current = 0;

    }


    public AppNotification(Context context, String CHANNEL_ID, String title, String content, int icon, int id) {
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));

        this.id = id;
        current = 0;

    }

    public void showNotify() {
        mBuilder.setProgress(100, 1, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void finishStatus() {
        mBuilder.setProgress(100, 100, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void updateStatus(int progress) {
        current = current + progress;
        mBuilder.setProgress(100, current, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void cancelNotify(int idCancel) {
        mNotifyManager.cancel(idCancel);
    }

    /**
     * Show the notification
     */
    public void show() {
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void setAction(PendingIntent pendingIntent) {
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void setSticky() {
//        AppNotification note = this.mBuilder.build();
//        note.flags |= AppNotification.FLAG_NO_CLEAR | AppNotification.FLAG_ONGOING_EVENT;
//        mNotifyManager.notify(id, note);
        mBuilder.setOngoing(true);
    }


}

package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import estg.ipp.pt.aroundtmegaesousa.R;

/**
 * Created by PC on 13/01/2018.
 */

public class NotificationUtils {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id;
    private int current;
    private Context context;

    public NotificationUtils(Context context, String CHANNEL_ID, String title, int icon, int id) {
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle(title)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
        setValues();
        this.id = id;
        current = 0;


    }

    public NotificationUtils(Context context, String CHANNEL_ID, String title, String content, int icon, int id) {
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
        setValues();
        this.id = id;
        current = 0;
        this.context = context;
    }

    public void setValues() {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notif = m.getBoolean("sound", false);
        boolean vibration = m.getBoolean("vibration", false);
        if (vibration) {
            mBuilder.setVibrate(new long[]{0, 1000, 1000, 1000, 1000});
        } else if (notif) {
            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        }

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

    public void notification() {
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void setAction(PendingIntent pendingIntent) {
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void setSticky() {
//        Notification note = this.mBuilder.build();
//        note.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
//        mNotifyManager.notify(id, note);
        mBuilder.setOngoing(true);
    }


}

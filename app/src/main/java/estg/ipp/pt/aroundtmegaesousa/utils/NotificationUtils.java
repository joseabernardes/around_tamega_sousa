package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import estg.ipp.pt.aroundtmegaesousa.R;

/**
 * Created by PC on 13/01/2018.
 */

public class NotificationUtils {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id;
    public NotificationUtils(){

    }

    public NotificationUtils(Context context, String CHANNEL_ID, String title, String content, int icon, int id) {
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon);
        this.id = id;

    }

    public void showNotify(){
        mBuilder.setProgress(100, 1, false);
        mNotifyManager.notify(id, mBuilder.build());

    }

    public void updateStatus(int progress) {
        mBuilder.setProgress(100, progress, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public void finalNotify(String finalmessage) {
        mBuilder.setContentText(finalmessage)
                .setProgress(0, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

}

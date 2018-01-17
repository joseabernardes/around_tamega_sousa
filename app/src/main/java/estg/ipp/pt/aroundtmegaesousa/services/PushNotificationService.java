package estg.ipp.pt.aroundtmegaesousa.services;

import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.utils.PrivateNotification;

/**
 * Created by PC on 17/01/2018.
 */

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "PushNotificationService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        PrivateNotification privateNotification = new PrivateNotification(this,remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(), R.drawable.logo_around,PrivateNotification.getRandomID());
        privateNotification.show();

    }


}

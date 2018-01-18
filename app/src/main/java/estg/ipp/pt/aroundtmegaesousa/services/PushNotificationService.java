package estg.ipp.pt.aroundtmegaesousa.services;

import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.utils.Enums;
import estg.ipp.pt.aroundtmegaesousa.utils.PrivateNotification;

/**
 * Created by PC on 17/01/2018.
 */

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "PushNotificationService";

    public static final String TOPIC = "notificacao";
    public static final String POI_ID = "poiID";
    public static final String POI_NAME = "poiName";
    public static final String CITY = "city";
    public static final String USER_ID = "userID";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null) {
            Map<String, String> payload = remoteMessage.getData();
            String poiID = payload.get(POI_ID);
            String poiName = payload.get(POI_NAME);
            String city = payload.get(CITY);
            String userID = payload.get(USER_ID);
        /*    if(poiID!=null &&)*/
          /*  FirebaseAuth.getInstance().getCurrentUser().getUid()*/
            PrivateNotification privateNotification = new PrivateNotification(this, "Ponto de Interesse adicionado", poiName + " - " + Enums.getCityByID(city), R.drawable.logo_around, PrivateNotification.getRandomID());
            privateNotification.show();
        }


    }



}

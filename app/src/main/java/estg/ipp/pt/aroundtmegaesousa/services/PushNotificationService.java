package estg.ipp.pt.aroundtmegaesousa.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.MainActivity;
import estg.ipp.pt.aroundtmegaesousa.activities.SettingsActivity;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
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

            if (poiID != null && poiName != null && city != null && userID != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && !userID.equals(user.getUid())) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(PointOfInterestFragment.DOCUMENT_ID, poiID);
                    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    PrivateNotification privateNotification = new PrivateNotification(this, "Ponto de Interesse adicionado", poiName + " - " + Enums.getCityByID(city), R.drawable.ic_add, PrivateNotification.getRandomID());
                    privateNotification.setAction(pi);
                    privateNotification.show();
                }
            }

        }
    }


}

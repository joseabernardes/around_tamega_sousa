package estg.ipp.pt.aroundtmegaesousa.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.io.File;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.MainActivity;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.interfaces.FirebaseServiceCommunication;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;
import estg.ipp.pt.aroundtmegaesousa.utils.PrivateNotification;

public class UploadFirebaseService extends Service implements FirebaseServiceCommunication {
    public static final String TAG = "UploadFirebaseService";
    public static final String START_UPLOAD_ACTION = "upload";
    public static final String CANCEL_UPLOAD_ACTION = "cancel_upload";
    public static final String POI_PARAM = "poi";
    public static final String FILES_PARAM = "files";
    private PrivateNotification progressNotification;
    private PrivateNotification resultNotification;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case START_UPLOAD_ACTION:
                PointOfInterest pointOfInterest = (PointOfInterest) intent.getSerializableExtra(POI_PARAM);
                List<File> photos = (List<File>) intent.getSerializableExtra(FILES_PARAM);
                FirebaseHelper firebaseHelper = new FirebaseHelper(this);
                createProgressNotification();
                firebaseHelper.addPointToFirebase(pointOfInterest, photos);
                break;
            case CANCEL_UPLOAD_ACTION:
                //cancelar
                //not used
                break;


        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (progressNotification != null) {
            progressNotification.cancel();
        }
        Toast.makeText(this, getString(R.string.message_toast_restart_service), Toast.LENGTH_SHORT).show();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void createProgressNotification() {
        Log.d(TAG, "createProgressNotification:");
        progressNotification = new PrivateNotification(this, getString(R.string.title_notification_add_point), R.mipmap.ic__cloud_upload, PrivateNotification.getRandomID());
        progressNotification.show();
    }

    @Override
    public void updateProgressNotification(double progress) {
        Log.d(TAG, "updateProgressNotification: " + progress);
        progressNotification.updateStatus(progress);
    }

    @Override
    public void createResultNotification(boolean result, String documentID, int resultCode) {
        Log.d(TAG, "createResultNotification: " + result);
        if (result) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(PointOfInterestFragment.DOCUMENT_ID, documentID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pi = PendingIntent.getActivity(this, PrivateNotification.getRandomID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            resultNotification = new PrivateNotification(this, getString(R.string.title_notification_add_point), getString(R.string.message_notification_added), R.mipmap.ic__check, PrivateNotification.getRandomID());
            resultNotification.setAction(pi);
        } else {
            String message = getString(R.string.message_notification_not_added);
            if (resultCode == FirebaseHelper.RESULT_FAIL_ADD_DATABASE) {
                message = getString(R.string.message_notification_not_added_database);
            } else if (resultCode == FirebaseHelper.RESULT_FAIL_UPLOAD_IMAGES) {
                message = getString(R.string.message_notification_not_added_upload);
            }
            resultNotification = new PrivateNotification(this, getString(R.string.title_notification_add_point), message, R.mipmap.ic__error, PrivateNotification.getRandomID());

        }

        progressNotification.cancel();
        resultNotification.show();
        stopSelf();//stop the service
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();

    }
}

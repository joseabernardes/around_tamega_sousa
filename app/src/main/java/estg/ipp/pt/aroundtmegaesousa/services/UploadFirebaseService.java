package estg.ipp.pt.aroundtmegaesousa.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.RandomActivity;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.AddPointTask;
import estg.ipp.pt.aroundtmegaesousa.utils.AppNotification;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;

public class UploadFirebaseService extends Service implements AddPointTask.FirebaseTaskCommunication {

    private static final int PROGRESS_NOTIFICATION_ID = 1;
    private static final int RESULT_NOTIFICATION_ID = 2;
    public static final String START_UPLOAD_ACTION = "upload";
    public static final String CANCEL_UPLOAD_ACTION = "cancel_upload";
    public static final String POI_PARAM = "poi";
    public static final String FILES_PARAM = "files";
    private AddPointTask task;
    private List<AppNotification> notifications;
    private int progressSteps;


    @Override
    public void onCreate() {
        super.onCreate();
        notifications = new ArrayList<>();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        switch (intent.getAction()) {
            case START_UPLOAD_ACTION:
                PointOfInterest pointOfInterest = (PointOfInterest) intent.getSerializableExtra(POI_PARAM);
                List<File> photos = (List<File>) intent.getSerializableExtra(FILES_PARAM);
                progressSteps = 60 / photos.size();
                task = new AddPointTask(pointOfInterest, photos, this);
                task.execute();


                break;
            case CANCEL_UPLOAD_ACTION:
                //cancelar
                task.cancel(true);
                break;


        }


        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void createProgressNotification() {
        AppNotification notification = new AppNotification(this, "Adicionar Ponto de Interesse", R.drawable.ic_check, PROGRESS_NOTIFICATION_ID);
        notifications.add(PROGRESS_NOTIFICATION_ID, notification);
        notification.show();
    }

    @Override
    public void updateProgressNotification() {
        notifications.get(PROGRESS_NOTIFICATION_ID).updateStatus(progressSteps);

    }

    @Override
    public void createResultNotification(boolean result, String documentID, int resultCode) {
        AppNotification notification = new AppNotification(this, AddPointTask.CHANNEL_ID, "Adicionar ponto", "Ponto adicionado com sucesso", R.drawable.logo_around, RESULT_NOTIFICATION_ID);
        notifications.add(RESULT_NOTIFICATION_ID, notification);
        notification.cancelNotify(PROGRESS_NOTIFICATION_ID);

        Intent intent = new Intent(this, RandomActivity.class);
        intent.putExtra("documentID", documentID);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (result) {
            notification.setAction(pi);
            notification.show();
        } else {
            String message = getString(R.string.message_snackbar_not_added);
            if (resultCode == FirebaseHelper.RESULT_FAIL_ADD_DATABASE) {
                message = getString(R.string.message_snackbar_not_added_database);
            } else if (resultCode == FirebaseHelper.RESULT_FAIL_UPLOAD_IMAGES) {
                message = getString(R.string.message_snackbar_not_added_upload);
            }
            AppNotification notificationError = new AppNotification(this, AddPointTask.CHANNEL_ID, "Adição de Ponto", message, R.drawable.logo_around, RESULT_NOTIFICATION_ID);
            notificationError.show();
        }

        stopSelf();
    }
}

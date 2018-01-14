package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.RandomActivity;
import estg.ipp.pt.aroundtmegaesousa.interfaces.FirebaseServiceCommunication;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.services.UploadFirebaseService;

/**
 * Created by PC on 12/01/2018.
 */

public class AddPointTask extends AsyncTask<String, Double, String> {
    public static final String TAG = UploadFirebaseService.TAG;
    private List<File> photos;
    private PointOfInterest pointOfInterest;
    private FirebaseHelper firebaseHelper;
    private FirebaseServiceCommunication mListener;


    public AddPointTask(PointOfInterest pointOfInterest, List<File> photos, FirebaseServiceCommunication mListener) {
        this.pointOfInterest = pointOfInterest;
        this.photos = photos;
        this.mListener = mListener;
        firebaseHelper = new FirebaseHelper(mListener);

    }


    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute: ");
        mListener.createProgressNotification();
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: ");
        firebaseHelper.addPointToFirebase(pointOfInterest, photos);


        return null;
    }


      /*  int notifyID = 001;

        final int inc = 60 / photos.size();

        final AppNotification notificationUtils = new AppNotification(context, CHANNEL_ID, "A adicionar Ponto...", R.drawable.logo_around, notifyID);
        notificationUtils.setSticky();
        final Intent intent = new Intent(context, RandomActivity.class);
        for (File file : photos) {
            List<byte[]> images = bitmapFileToBytes(file);
            String uID = UUID.randomUUID().toString();
            String path = FirebaseHelper.PHOTOS_DIRECTORY + uID + ".jpeg";
            StorageReference photoRef = storage.getReference(path);
            UploadTask uploadTask = photoRef.putBytes(images.get(0));
            notificationUtils.showNotify();
/*            //Thumbnail
            String thumbPath = FirebaseHelper.PHOTOS_DIRECTORY + uID + "_thumb.jpeg";
            StorageReference thumbRef = storage.getReference(thumbPath);
            UploadTask uploadThumbTask = photoRef.putBytes(images.get(1));
            uploadThumbTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        notificationUtils.updateStatus(inc);
                        synchronized (photosURL) {
                            photosThumbURL.add(task.getResult().getDownloadUrl().toString());
                            if (photosURL.size() == photos.size()) { //se já fez upload de todas as fotos
                                pointOfInterest.setPhotos(photosURL);
                                pointOfInterest.setDate(Calendar.getInstance().getTime());
                                notificationUtils.finishStatus();
                                new FirebaseHelper(context).addPointToDatabase(pointOfInterest);
                            }
                        }
                    } else {
                        context.addPointResult(false, null, FirebaseHelper.RESULT_FAIL_UPLOAD_IMAGES);
                    }
                }
            });*/
/*
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        notificationUtils.updateStatus(inc);
                        synchronized (photosURL) {
                            photosURL.add(task.getResult().getDownloadUrl().toString());
                            if (photosURL.size() == photos.size()) { //se já fez upload de todas as fotos
                                pointOfInterest.setPhotos(photosURL);
                                pointOfInterest.setDate(Calendar.getInstance().getTime());
                                notificationUtils.finishStatus();
                                new FirebaseHelper(context).addPointToDatabase(pointOfInterest);
                            }
                        }
                    } else {
                        context.addPointResult(false, null, FirebaseHelper.RESULT_FAIL_UPLOAD_IMAGES);
                    }
                }
            });

        }*/
}

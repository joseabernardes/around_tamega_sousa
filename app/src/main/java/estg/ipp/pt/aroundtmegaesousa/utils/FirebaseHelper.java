package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.internal.FirebaseAppHelper;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.BaseActivity;
import estg.ipp.pt.aroundtmegaesousa.activities.RandomActivity;
import estg.ipp.pt.aroundtmegaesousa.interfaces.FirebaseServiceCommunication;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.services.UploadFirebaseService;

/**
 * Created by José Bernardes on 11/01/2018.
 */

public class FirebaseHelper {

    public static final String TAG = UploadFirebaseService.TAG;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAIL_UPLOAD_IMAGES = 2;
    public static final int RESULT_FAIL_ADD_DATABASE = 3;
    public static final String POINTS_COLLECTION = "points";
    public static final String PHOTOS_DIRECTORY = "photos";
    public static final float PHOTOS_PERCENTAGE = 0.75f;
    public static final float DATABASE_PERCENTAGE = 0.25f;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference points;
    private FirebaseServiceCommunication mListener;
    private List<String> photosURL;
    private List<String> photosThumbURL;
    private float photosProgressSteps;
    private double current;


    public FirebaseHelper(FirebaseServiceCommunication mListener) {
        this.mListener = mListener;
        db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        points = db.collection(POINTS_COLLECTION);
    }


    public void addPointToFirebase(PointOfInterest pointOfInterest, List<File> photos) {
        photosProgressSteps = PHOTOS_PERCENTAGE / photos.size();
        current = 0;
        addImagesToStorage(photos, pointOfInterest);
    }


    private void addPointToDatabase(PointOfInterest point) {
        Log.d(TAG, "addPointToDatabase: ");
        points.add(point).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {

                    mListener.updateProgressNotification(current + (100 * DATABASE_PERCENTAGE));
                    DocumentReference documentReference = task.getResult();
                    mListener.createResultNotification(true, documentReference.getId(), RESULT_SUCCESS);
                } else {
                    mListener.createResultNotification(false, null, RESULT_FAIL_ADD_DATABASE);
                }
            }
        });
    }


    private void addImagesToStorage(final List<File> photos, final PointOfInterest pointOfInterest) {
        Log.d(TAG, "addImagesToStorage: ");
        this.photosURL = new ArrayList<>();
        this.photosThumbURL = new ArrayList<>();
        for (File file : photos) {
            List<byte[]> images = bitmapFileToBytes(file);
            String uID = UUID.randomUUID().toString();
            String path = FirebaseHelper.PHOTOS_DIRECTORY + uID + ".jpeg";
            StorageReference photoRef = storage.getReference(path);
            UploadTask uploadTask = photoRef.putBytes(images.get(0));

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        synchronized (photosURL) {
                            current = current + (photosProgressSteps * 100);
                            photosURL.add(task.getResult().getDownloadUrl().toString());
                            if (photosURL.size() == photos.size()) { //se já fez upload de todas as fotos
                                pointOfInterest.setPhotos(photosURL);
                                pointOfInterest.setDate(Calendar.getInstance().getTime());
                                addPointToDatabase(pointOfInterest);
                            }
                        }
                    } else {
                        mListener.createResultNotification(false, null, FirebaseHelper.RESULT_FAIL_UPLOAD_IMAGES);
                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    mListener.updateProgressNotification(current + (progress * photosProgressSteps));
                }
            });

        }


    }


    private List<byte[]> bitmapFileToBytes(File file) {
        List<byte[]> images = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        images.add(baos.toByteArray());
        baos = new ByteArrayOutputStream();
        Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmap, 256, 256);
        thumb.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        images.add(baos.toByteArray());
        return images;
    }

    public interface FirebaseCommunication {
        void addPointResult(boolean result, String documentID, int resultCode);

        void updateProgress(double progress);

    }
}
          /*  notificationUtils.showNotify();*/
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

package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
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
import estg.ipp.pt.aroundtmegaesousa.activities.AddPointActivity;
import estg.ipp.pt.aroundtmegaesousa.activities.BaseActivity;
import estg.ipp.pt.aroundtmegaesousa.activities.RandomActivity;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;

/**
 * Created by PC on 12/01/2018.
 */

public class AddPointTask extends AsyncTask<String, String, String> {

    public static final String CHANNEL_ID = "firestore";
    private List<File> photos;
    private FirebaseStorage storage;
    private List<String> photosURL;
    private List<String> photosThumbURL;
    private BaseActivity context;
    private PointOfInterest pointOfInterest;

    public AddPointTask(PointOfInterest pointOfInterest, List<File> photos, BaseActivity context) {
        this.pointOfInterest = pointOfInterest;
        this.photos = photos;
        this.photosURL = new ArrayList<>();
        this.photosThumbURL = new ArrayList<>();
        this.context = context;
        this.storage = FirebaseStorage.getInstance();

    }

    @Override
    protected String doInBackground(String... strings) {
        int notifyID = 001;
        int incr = 0;

        switch (photos.size()) {
            case 1:
                incr = 50;
                break;
            case 2:
                incr = 25;
                break;
            case 3:
                incr = 18;
                break;
            case 4:
                incr = 12;
                break;
            case 5:
                incr = 10;
                break;
        }

        final int inc = incr;

        final NotificationUtils notificationUtils = new NotificationUtils(context, CHANNEL_ID, "A adicionar Ponto...", R.drawable.logo_around, notifyID);
        notificationUtils.setSticky();
        final Intent intent = new Intent(context, RandomActivity.class);
        for (File file : photos) {
            List<byte[]> images = bitmapFileToBytes(file);
            String uID = UUID.randomUUID().toString();
            String path = FirestoreHelper.PHOTOS_DIRECTORY + uID + ".jpeg";
            StorageReference photoRef = storage.getReference(path);
            UploadTask uploadTask = photoRef.putBytes(images.get(0));
            notificationUtils.showNotify();
/*            //Thumbnail
            String thumbPath = FirestoreHelper.PHOTOS_DIRECTORY + uID + "_thumb.jpeg";
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
                                new FirestoreHelper(context).addPointToDatabase(pointOfInterest);
                            }
                        }
                    } else {
                        context.addPointResult(false, null, FirestoreHelper.RESULT_FAIL_UPLOAD_IMAGES);
                    }
                }
            });*/

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
                                new FirestoreHelper(context).addPointToDatabase(pointOfInterest);
                            }
                        }
                    } else {
                        context.addPointResult(false, null, FirestoreHelper.RESULT_FAIL_UPLOAD_IMAGES);
                    }
                }
            });

        }
        return null;
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
}

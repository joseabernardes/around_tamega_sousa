package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

import estg.ipp.pt.aroundtmegaesousa.activities.AddPointActivity;
import estg.ipp.pt.aroundtmegaesousa.activities.BaseActivity;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;

/**
 * Created by PC on 12/01/2018.
 */

public class AddPointTask extends AsyncTask<String, String, String> {


    private List<File> photos;
    private FirebaseStorage storage;
    private List<String> photosURL;
    private BaseActivity context;
    private PointOfInterest pointOfInterest;

    public AddPointTask(PointOfInterest pointOfInterest, List<File> photos, BaseActivity context) {
        this.pointOfInterest = pointOfInterest;
        this.photos = photos;
        this.photosURL = new ArrayList<>();
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @Override
    protected String doInBackground(String... strings) {
        for (File file : photos) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            String path = FirestoreHelper.PHOTOS_DIRECTORY + UUID.randomUUID() + ".jpeg";
            StorageReference photoRef = storage.getReference(path);
            UploadTask uploadTask = photoRef.putBytes(data);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        synchronized (photosURL) {
                            photosURL.add(task.getResult().getDownloadUrl().toString());
                            if (photosURL.size() == photos.size()) { //se já fez upload de todas as fotos
                                pointOfInterest.setPhotos(photosURL);
                                pointOfInterest.setDate(Calendar.getInstance().getTime());
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
}

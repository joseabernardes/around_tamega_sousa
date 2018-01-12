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
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;

/**
 * Created by PC on 12/01/2018.
 */

public class AddPointTask extends AsyncTask<String, String, String> {

    private String name;
    private String description;
    private String userID;
    private LatLng coordinates;
    private String cityID;
    private int typeID;
    private int count;
    private List<File> photos;
    private FirebaseStorage storage;
    private List<String> photosURL;
    private Activity context;

    public AddPointTask(String name, String desc, LatLng coordinates, String cityID, int typeID, String userID, int count, List<File> photos, Activity context) {
        this.name = name;
        this.description = desc;
        this.cityID = cityID;
        this.coordinates = coordinates;
        this.typeID = typeID;
        this.userID = userID;
        this.count = count;
        this.photos = photos;
        this.photosURL = new ArrayList<String>();
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @Override
    protected String doInBackground(String... strings) {

        for (File file : this.photos) {
            if (file != null) {
//                photos.add(file.getAbsolutePath());
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                String path = "photos/" + UUID.randomUUID() + ".png";
                final StorageReference photoRef = storage.getReference(path);


                UploadTask uploadTask = photoRef.putBytes(data);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            synchronized (photosURL) {
                                photosURL.add(task.getResult().getDownloadUrl().toString());
                                if (photosURL.size() == count) {
                                    PointOfInterest pointOfInterest = new PointOfInterest(name, description, coordinates, cityID, typeID, photosURL, userID, Calendar.getInstance().getTime());
                                    new FirestoreHelper().addPoint(context, pointOfInterest);
                                }
                            }
                        } else {
                            Toast.makeText(context, "RAIA", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        return null;
    }
}

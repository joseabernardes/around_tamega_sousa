package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.internal.FirebaseAppHelper;

import estg.ipp.pt.aroundtmegaesousa.activities.BaseActivity;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;

/**
 * Created by José Bernardes on 11/01/2018.
 */

public class FirestoreHelper {

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAIL_UPLOAD_IMAGES = 2;
    public static final int RESULT_FAIL_ADD_DATABASE = 3;
    public static final String POINTS_COLLECTION = "points";
    public static final String PHOTOS_DIRECTORY = "photos";
    private FirebaseFirestore db;
    private CollectionReference points;
    private BaseActivity context;


    public FirestoreHelper(BaseActivity context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        points = db.collection(POINTS_COLLECTION);
    }


    public void addPointToDatabase(PointOfInterest point) {
        points.add(point).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReference = task.getResult();
                    context.addPointResult(true, documentReference.getId(), RESULT_SUCCESS);
                } else {
                    context.addPointResult(false, null, RESULT_FAIL_ADD_DATABASE);
                }
            }
        });
    }

    public interface FirestoreCommunication {
        void addPointResult(boolean result, String documentID, int resultCode);

    }
}

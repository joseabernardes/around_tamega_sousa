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

import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;

/**
 * Created by José Bernardes on 11/01/2018.
 */

public class FirestoreHelper {

    private FirebaseFirestore db;
    public static final String POINTS_COLLECTION = "points";
    private CollectionReference points;


    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        points = db.collection(POINTS_COLLECTION);
    }


    public void addPoint(final FirestoreCommunication context, PointOfInterest point) {
        points.add(point).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                boolean result = false;
                String id = null;
                if (task.isSuccessful()) {
                    DocumentReference documentReference = task.getResult();
                    id = documentReference.getId();
                    result = true;
                }
                context.addPointResult(result, id);

            }
        });
    }

    public interface FirestoreCommunication {
        void addPointResult(boolean result, String id);

    }
}

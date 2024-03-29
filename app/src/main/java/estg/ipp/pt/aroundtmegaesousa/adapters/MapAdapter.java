package estg.ipp.pt.aroundtmegaesousa.adapters;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.interfaces.FirebaseServiceCommunication;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.services.UploadFirebaseService;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;

/**
 * Created by José Bernardes on 17/01/2018.
 */

public class MapAdapter implements EventListener<QuerySnapshot> {

    private static final String TAG = "ListMapFragment";

    private List<PointOfInterest> pointOfInterestList;
    private Query query;
    private ListenerRegistration mRegistration;
    private onItemsChangeListener onItemsChangeListener;


    public MapAdapter(Query query, onItemsChangeListener onItemsChangeListener) {
        this.query = query;
        this.onItemsChangeListener = onItemsChangeListener;
        this.pointOfInterestList = new ArrayList<>();
    }


    /**
     * Escutar alterações no firestore
     */
    public void startListening() {
        Log.d(TAG, "startListening: ");
        if (query != null && mRegistration == null) {
            mRegistration = query.addSnapshotListener(this);
        }
    }

    /**
     * Parar escuta de alterações
     */
    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
        pointOfInterestList.clear();
        onItemsChangeListener.removeAllMarkers();
    }


    /**
     * Iniciar nova pesquisa considerando os parametros da query
     *
     * @param query
     */
    public void setQuery(Query query) {
        Log.d(TAG, "setQuery: ");
        stopListening();
        pointOfInterestList.clear();
        this.query = query;
        startListening();
    }

    /**
     * Listener da query
     *
     * @param documentSnapshots
     * @param e
     */
    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        Log.d(TAG, "onEvent: STARTS");
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            onError(e);
            return;
        }

        // Dispatch the event
        Log.d(TAG, "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            PointOfInterest pointOfInterest = change.getDocument().toObject(PointOfInterest.class);
            pointOfInterest.setId(change.getDocument().getId());
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(pointOfInterest, change.getNewIndex());
                    break;
                case MODIFIED:
                    onDocumentModified(pointOfInterest, change.getNewIndex(), change.getOldIndex());
                    break;
                case REMOVED:
                    onDocumentRemoved(change.getOldIndex());
                    break;
            }
        }
        onItemsChangeListener.addItemToMap(pointOfInterestList);
        Log.d(TAG, "onEvent: END");
    }


    protected void onDocumentAdded(PointOfInterest pointOfInterest, int changeNextIndex) {
        pointOfInterestList.add(changeNextIndex, pointOfInterest);
    }

    protected void onDocumentModified(PointOfInterest pointOfInterest, int changeNextIndex, int changeOldIndex) {
        if (changeOldIndex == changeNextIndex) {
            // Mantem a mesma posição
            pointOfInterestList.set(changeOldIndex, pointOfInterest);
        } else {
            pointOfInterestList.remove(changeOldIndex);
            pointOfInterestList.add(changeNextIndex, pointOfInterest);
        }
    }

    protected void onDocumentRemoved(int changeOldIndex) {
        pointOfInterestList.remove(changeOldIndex);
    }


    protected void onError(FirebaseFirestoreException e) {
        Log.d(TAG, "onError: ");
    }


    public interface onItemsChangeListener {

        void addItemToMap(List<PointOfInterest> pointOfInterests);

        void removeAllMarkers();

    }

    

}

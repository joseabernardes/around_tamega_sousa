package estg.ipp.pt.aroundtmegaesousa.adapters;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;

/**
 * Created by José Bernardes on 17/01/2018.
 */

public class MapAdapter implements EventListener<QuerySnapshot> {

    private static final String TAG = "MapAdapter";

    private List<PointOfInterest> pointOfInterestList;
    private Query query;
    private ListenerRegistration mRegistration;
    private onItemsChangeListener onItemsChangeListener;


    public MapAdapter(Query query, onItemsChangeListener onItemsChangeListener) {
        this.query = query;
        this.onItemsChangeListener = onItemsChangeListener;
        this.pointOfInterestList = new ArrayList<>();
    }

    protected PointOfInterest getPointOfInterest(int index) {
        return pointOfInterestList.get(index);
    }


    /**
     * Escutar alterações no firestore
     */
    public void startListening() {
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
        stopListening();
        pointOfInterestList.clear();
//        notifyDataSetChanged();
        //render
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
    }


    protected void onDocumentAdded(PointOfInterest pointOfInterest, int changeNextIndex) {
        pointOfInterestList.add(changeNextIndex, pointOfInterest);
        onItemsChangeListener.addItemToMap(pointOfInterestList);
    }

    protected void onDocumentModified(PointOfInterest pointOfInterest, int changeNextIndex, int changeOldIndex) {
        if (changeOldIndex == changeNextIndex) {
            // Mantem a mesma posição
            pointOfInterestList.set(changeOldIndex, pointOfInterest);
            onItemsChangeListener.addItemToMap(pointOfInterestList);
        } else {
            pointOfInterestList.remove(changeOldIndex);
            pointOfInterestList.add(changeNextIndex, pointOfInterest);
            onItemsChangeListener.addItemToMap(pointOfInterestList);
        }
    }

    protected void onDocumentRemoved(int changeOldIndex) {
        pointOfInterestList.remove(changeOldIndex);
        onItemsChangeListener.addItemToMap(pointOfInterestList);
    }


    protected void onError(FirebaseFirestoreException e) {
        Log.d(TAG, "onError: ");
    }


    public interface onItemsChangeListener {

        void addItemToMap(List<PointOfInterest> pointOfInterests);

        void removeAllMarkers();

    }


}

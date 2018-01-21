package estg.ipp.pt.aroundtmegaesousa.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;

/**
 * Created by José Bernardes on 13/01/2018.
 */

public abstract class PointOfInterestAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> implements EventListener<QuerySnapshot> {
    private static final String TAG = "PointOfInterestAdapter";
    private List<PointOfInterest> pointOfInterestList;
    private Query query;
    private ListenerRegistration mRegistration;

    public PointOfInterestAdapter(Query query) {
        this.query = query;
        this.pointOfInterestList = new ArrayList<>();
    }


    protected PointOfInterest getPointOfInterest(int index) {
        return pointOfInterestList.get(index);
    }

    @Override
    public int getItemCount() {
        return pointOfInterestList.size();
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
        notifyDataSetChanged();
    }


    /**
     * Iniciar nova pesquisa considerando os parametros da query
     *
     * @param query
     */
    public void setQuery(Query query) {
        stopListening();
        pointOfInterestList.clear();
        notifyDataSetChanged();
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
            onDocumentChange(pointOfInterest, change.getType(), change.getNewIndex(), change.getOldIndex());
        }
    }


    public void onDocumentChange(PointOfInterest pointOfInterest, DocumentChange.Type type, int nextIndex, int oldIndex) {
        switch (type) {
            case ADDED:
                onDocumentAdded(pointOfInterest, nextIndex);
                break;
            case MODIFIED:
                onDocumentModified(pointOfInterest, nextIndex, oldIndex);
                break;
            case REMOVED:
                onDocumentRemoved(oldIndex);
                break;
        }
    }


    protected void onDocumentAdded(PointOfInterest pointOfInterest, int changeNextIndex) {
        pointOfInterestList.add(changeNextIndex, pointOfInterest);
        notifyItemInserted(changeNextIndex);
    }

    protected void onDocumentModified(PointOfInterest pointOfInterest, int changeNextIndex, int changeOldIndex) {
        if (changeOldIndex == changeNextIndex) {
            // Mantem a mesma posição
            pointOfInterestList.set(changeOldIndex, pointOfInterest);
            notifyItemChanged(changeOldIndex);
        } else {
            pointOfInterestList.remove(changeOldIndex);
            pointOfInterestList.add(changeNextIndex, pointOfInterest);
            notifyItemMoved(changeOldIndex, changeNextIndex);
        }
    }

    protected void onDocumentRemoved(int changeOldIndex) {
        pointOfInterestList.remove(changeOldIndex);
        notifyItemRemoved(changeOldIndex);
    }

    protected abstract void onError(FirebaseFirestoreException e);

}

package estg.ipp.pt.aroundtmegaesousa.interfaces;

import estg.ipp.pt.aroundtmegaesousa.models.Rating;

/**
 * Created by José Bernardes on 20/01/2018.
 */

public interface FirebaseResultListener {

    String CHECK_RATING = "check_rating";
    String EDIT_RATING = "edit_rating";
    String ADD_RATING = "add_rating";
    String DELETE_POI = "delete_poi";
    String ADD_FAVORITE= "add_favorite";
    String REMOVE_FAVORITE= "remove_favorite";

    void firebaseTaskResult(boolean result, String type);

    void firebaseRatingTaskResult(Rating rating);


}

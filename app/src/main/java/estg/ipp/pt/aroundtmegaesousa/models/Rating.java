package estg.ipp.pt.aroundtmegaesousa.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Exclude;

/**
 * Created by PC on 16/01/2018.
 */

public class Rating {

    @Exclude
    private String idRating;
    private String id;
    private float rating;

    public Rating() {

    }

    public Rating(String user, float rating) {
        this.id = user;
        this.rating = rating;
    }

    public String getIdRating() {
        return idRating;
    }

    public void setIdRating(String idRating) {
        this.idRating = idRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

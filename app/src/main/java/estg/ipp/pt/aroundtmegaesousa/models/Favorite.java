package estg.ipp.pt.aroundtmegaesousa.models;

import com.google.firebase.firestore.Exclude;

/**
 * Created by PC on 16/01/2018.
 */

public class Favorite {


    @Exclude String idFavorites;
    String idUser;

    public Favorite() {

    }

    public Favorite(String user) {
        this.idUser = user;

    }

    public String getIdFavorites() {
        return idFavorites;
    }

    public void setIdFavorites(String idFavorites) {
        this.idFavorites = idFavorites;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}

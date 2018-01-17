package estg.ipp.pt.aroundtmegaesousa.models;

import com.google.firebase.firestore.Exclude;

/**
 * Created by PC on 16/01/2018.
 */

public class Favorite {


    @Exclude String idFavorites;
    String idUser;
    String idPOI;

    public Favorite() {

    }

    public Favorite(String user,String idPOI) {
        this.idUser = user;
        this.idPOI = idPOI;

    }

    public String getIdFavorites() {
        return idFavorites;
    }

    public String getIdPOI() {
        return idPOI;
    }

    public void setIdPOI(String idPOI) {
        this.idPOI = idPOI;
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

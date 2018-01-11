package estg.ipp.pt.aroundtmegaesousa.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;


public class PointOfInterest {

    private int id;
    private String name;
    private String description;
    private GeoPoint location;
    private String typeOfLocation;
    private List<String> photos;
    private String user;
    private int avgRatting;
    /*
    private List<Map<String, Object>> classifications; //array de objetos
    */

    public PointOfInterest() {
    }

    public PointOfInterest(String name) {
        this.name = name;
    }

    public PointOfInterest(String name, String description, LatLng location, String typeOfLocation, List<String> photos, String user) {
        this.name = name;
        this.description = description;
        this.location = new GeoPoint(location.latitude, location.longitude);
        this.typeOfLocation = typeOfLocation;
        this.photos = photos;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getTypeOfLocation() {
        return typeOfLocation;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public String getUser() {
        return user;
    }

    public int getAvgRatting() {
        return avgRatting;
    }


}

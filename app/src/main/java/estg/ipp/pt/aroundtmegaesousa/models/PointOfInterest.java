package estg.ipp.pt.aroundtmegaesousa.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.List;


public class PointOfInterest {

    private String name;
    private String description;
    private GeoPoint location;
    private int typeOfLocation;
    private List<String> photos;
    private String user;
    private int avgRatting;
    private Date date;
    private String city;
    /*
    private List<Map<String, Object>> classifications; //array de objetos
    */

    public PointOfInterest() {
    }

    public PointOfInterest(String name) {
        this.name = name;
    }

    /**
     * Constructor with the list of images
     *
     * @param name
     * @param description
     * @param location
     * @param typeOfLocation
     * @param photos
     * @param user
     * @param date
     */
    public PointOfInterest(String name, String description, LatLng location, String city, int typeOfLocation, List<String> photos, String user, Date date) {
        this.name = name;
        this.description = description;
        this.location = new GeoPoint(location.latitude, location.longitude);
        this.typeOfLocation = typeOfLocation;
        this.photos = photos;
        this.user = user;
        this.date = date;
        this.city = city;

    }

    /**
     * Constructor without the list of images and the date
     *
     * @param name
     * @param description
     * @param location
     * @param typeOfLocation
     * @param user
     */
    public PointOfInterest(String name, String description, LatLng location, String city, int typeOfLocation, String user) {
        this.name = name;
        this.description = description;
        this.location = new GeoPoint(location.latitude, location.longitude);
        this.typeOfLocation = typeOfLocation;
        this.photos = photos;
        this.user = user;
        this.date = date;
        this.city = city;

    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public Date getDate() {
        return date;
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

    public int getTypeOfLocation() {
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

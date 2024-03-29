package estg.ipp.pt.aroundtmegaesousa.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PointOfInterest implements Serializable {


    public static final String FIELD_CITY = "city";
    public static final String FIELD_TYPE_OF_LOCATION = "typeOfLocation";
    public static final String FIELD_DATE= "date";
    public static final String FIELD_AVG_RATING = "avgRating";



    @Exclude
    private String id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private int typeOfLocation;
    private List<String> photos;
    private List<String> photosThumbs;
    private String user;
    private float avgRating;
    private int numRatings;
    private Date date;
    private String city;
    private Map<String,Date> favorites;
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
     * @param numRatings
     */
    public PointOfInterest(String name, String description, LatLng location, String city, int typeOfLocation, List<String> photos, List<String> photosThumbs, String user, Date date, int numRatings) {
        this.name = name;
        this.description = description;
        this.latitude = location.latitude;
        this.longitude = location.longitude;
        this.typeOfLocation = typeOfLocation;
        this.photos = photos;
        this.photosThumbs = photosThumbs;
        this.user = user;
        this.date = date;
        this.city = city;
        this.numRatings = numRatings;
        this.favorites = new HashMap<>();

    }

    /**
     * Constructor without the list of images and the date
     *
     * @param name
     * @param description
     * @param location
     * @param typeOfLocation
     * @param user
     * @param numRatings
     */
    public PointOfInterest(String name, String description, LatLng location, String city, int typeOfLocation, String user, int numRatings) {
        this.name = name;
        this.description = description;
        this.latitude = location.latitude;
        this.longitude = location.longitude;
        this.typeOfLocation = typeOfLocation;
        this.user = user;
        this.city = city;
        this.numRatings = numRatings;
        this.favorites = new HashMap<>();

    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<String> getPhotosThumbs() {
        return photosThumbs;
    }

    public void setPhotosThumbs(List<String> photosThumbs) {
        this.photosThumbs = photosThumbs;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng pointLocation() {
        return new LatLng(this.latitude, this.longitude);
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

    public float getAvgRating() {
        return avgRating;
    }

    public Map<String, Date> getFavorites() {
        return favorites;
    }

    public void setFavorites(Map<String, Date> favorites) {
        this.favorites = favorites;
    }
}

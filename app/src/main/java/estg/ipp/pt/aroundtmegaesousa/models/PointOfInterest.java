package estg.ipp.pt.aroundtmegaesousa.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PointOfInterest implements Serializable {

    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private int typeOfLocation;
    private List<String> photos;
    private String user;
    private float avgRatting;
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
        this.latitude = location.latitude;
        this.longitude = location.longitude;
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
        this.latitude = location.latitude;
        this.longitude = location.longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getLocation() {
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

    public float getAvgRatting() {
        return avgRatting;
    }

    public List<String> getPhotoThumbs() {
        List<String> thumbs = new ArrayList<>();
        for (String thumb : photos) {
            thumbs.add(convertPhotoURL(thumb));
        }
        return thumbs;
    }

    public String getFirstPhotoThumb() {
        String photo = photos.get(0);
        if (photo != null) {
            return convertPhotoURL(photo);
        } else {
            return null;
        }
    }

    private String convertPhotoURL(String url) {
        int last = url.lastIndexOf('.');
        return url.substring(0, last) + "_250_thumb." + url.substring(last);
    }

}

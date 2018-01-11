package estg.ipp.pt.aroundtmegaesousa.models;

import java.io.Serializable;

/**
 * Created by José Bernardes on 02/01/2018.
 */

public class City implements Serializable {

    private String id;
    private String name;
    private int geoJsonFileID;

    public City(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public City(String id, String name, int geoJsonFileID) {
        this.id = id;
        this.name = name;
        this.geoJsonFileID = geoJsonFileID;
    }

    public int getGeoJsonFileID() {
        return geoJsonFileID;
    }

    public void setGeoJsonFileID(int geoJsonFileID) {
        this.geoJsonFileID = geoJsonFileID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

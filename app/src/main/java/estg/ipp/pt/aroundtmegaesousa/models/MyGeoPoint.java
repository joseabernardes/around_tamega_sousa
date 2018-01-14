package estg.ipp.pt.aroundtmegaesousa.models;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

/**
 * Created by José Bernardes on 14/01/2018.
 */

public class MyGeoPoint extends GeoPoint implements Serializable {


    public MyGeoPoint(double v, double v1) {
        super(v, v1);
    }
}

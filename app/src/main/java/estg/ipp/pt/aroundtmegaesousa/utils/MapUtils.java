package estg.ipp.pt.aroundtmegaesousa.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by José Bernardes on 30/12/2017.
 */

public class MapUtils {


    public static boolean containsLocation(GeoJsonLayer geoJsonLayer, LatLng point) {
        boolean result = false;
        Iterator<GeoJsonFeature> iterator = geoJsonLayer.getFeatures().iterator();
        if (iterator.hasNext()) { //sei que os ficheiros tem apenas uma feature!
            List<List<LatLng>> s = (List<List<LatLng>>) iterator.next().getGeometry().getGeometryObject();
            if (!s.isEmpty()) {
                List<LatLng> coordinates = s.get(0);
                result = PolyUtil.containsLocation(point, coordinates, false);
            }
        }
        return result;
    }

    public static List<LatLng> getListFromGeoJson(GeoJsonLayer geoJsonLayer) {

        Iterator<GeoJsonFeature> iterator = geoJsonLayer.getFeatures().iterator();
        if (iterator.hasNext()) { //sei que os ficheiros tem apenas uma feature!
            List<List<LatLng>> s = (List<List<LatLng>>) iterator.next().getGeometry().getGeometryObject();
            if (!s.isEmpty()) {
                return s.get(0);

            }
        }
        return new ArrayList<LatLng>();
    }
}

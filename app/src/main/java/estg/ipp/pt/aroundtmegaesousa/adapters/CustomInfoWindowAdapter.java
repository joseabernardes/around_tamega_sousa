package estg.ipp.pt.aroundtmegaesousa.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.TypeOfLocation;
import estg.ipp.pt.aroundtmegaesousa.utils.Enums;

/**
 * Created by José Bernardes on 19/01/2018.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public CustomInfoWindowAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.layout_map_info_window, null);
        TextView title = view.findViewById(R.id.title);
        TextView type = view.findViewById(R.id.type);
        AppCompatRatingBar ratingBar = view.findViewById(R.id.rating_bar_map);
        PointOfInterest pointOfInterest = (PointOfInterest) marker.getTag();
        title.setText(pointOfInterest.getName());
        TypeOfLocation typeOfLocation = Enums.getTypeOfLocationByID(pointOfInterest.getTypeOfLocation());
        type.setText(typeOfLocation.getType());
        ratingBar.setRating(pointOfInterest.getAvgRating());
        return view;
    }
}
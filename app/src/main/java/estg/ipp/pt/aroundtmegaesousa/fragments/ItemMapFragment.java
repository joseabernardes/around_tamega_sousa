package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.adapters.CustomInfoWindowAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsCommunicationListener;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;


public class ItemMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private PointOfInterest pointOfInterest;
    private SupportMapFragment mMapFragment;
    private GeoJsonLayer tamega;
    private GoogleMap mGoogleMap;
    private OnFragmentsCommunicationListener mContext;

    public ItemMapFragment() {
    }


    public static ItemMapFragment newInstance(PointOfInterest pointOfInterest) {
        ItemMapFragment fragment = new ItemMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, pointOfInterest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pointOfInterest = (PointOfInterest) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View mContentView = inflater.inflate(R.layout.fragment_item_map, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        mContext.changeActionBarTitle(pointOfInterest.getName(),false);
        mContext.showFloatingButton(false);

        return mContentView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_item_map, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_googleMaps:
                Uri gmmIntentUri = Uri.parse("geo:" + pointOfInterest.getLatitude() + "," + pointOfInterest.getLongitude() + "?q=" + pointOfInterest.getLatitude() + "," + pointOfInterest.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentsCommunicationListener) {
            mContext = (OnFragmentsCommunicationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                zoomToLocation(marker.getPosition(), 16);
                marker.showInfoWindow();
                return true;
            }
        });


        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter((Activity) mContext);
        mGoogleMap.setInfoWindowAdapter(adapter);


        //desenhar pontos!
        addMarker(pointOfInterest);

        try {
            tamega = new GeoJsonLayer(mGoogleMap, R.raw.tamegaesousa, (Context) mContext);
            GeoJsonPolygonStyle style = tamega.getDefaultPolygonStyle();
            style.setStrokeColor(ContextCompat.getColor((Context) mContext, R.color.colorPrimaryDark));
            style.setStrokeWidth(6f);
            tamega.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addMarker(PointOfInterest pointOfInterest) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic__map_marker);
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(pointOfInterest.getLocation())
                .title(pointOfInterest.getName())
                .icon(icon));

        marker.setTag(pointOfInterest);
    }

    private void zoomToLocation(LatLng latLng, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }


}
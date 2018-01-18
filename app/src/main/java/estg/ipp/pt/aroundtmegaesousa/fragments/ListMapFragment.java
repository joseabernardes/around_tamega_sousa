package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.adapters.MapAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsCommunicationListener;
import estg.ipp.pt.aroundtmegaesousa.models.Filters;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;


public class ListMapFragment extends Fragment implements OnMapReadyCallback, MapAdapter.onItemsChangeListener, FilterDialogFragment.FilterListener {

    private static final String TAG = "ListMapFragment";
    public static final String FILTER = "filter";
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Context mContext;
    private OnFragmentsCommunicationListener communicationListener;
    private View filterBar;
    private TextView currentSearch;
    private TextView currentSortBy;
    private ImageView buttonCancel;
    private Filters mFilters;
    private FilterDialogFragment mFilterDialog;
    private FirebaseFirestore mFirestore;
    private GeoJsonLayer tamega;
    private MapAdapter mapAdapter;
    private Query query;
    List<Marker> markers;


    public ListMapFragment() {

    }


    public static ListMapFragment newInstance(String param1, String param2) {
        ListMapFragment fragment = new ListMapFragment();
        Bundle args = new Bundle();
/*        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
/*            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_map_view, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        filterBar = mContentView.findViewById(R.id.filter_bar);
        currentSearch = mContentView.findViewById(R.id.text_current_search);
        currentSortBy = mContentView.findViewById(R.id.text_current_sort_by);
        buttonCancel = mContentView.findViewById(R.id.button_clear_filter);

        filterBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterDialog.resetFilters();
                mFilters = Filters.getDefault();
                onFilter(mFilters);
                mFilterDialog = new FilterDialogFragment();

            }
        });

        mFilterDialog = new FilterDialogFragment();
        markers = new ArrayList<>();
        if (communicationListener != null) {
            communicationListener.changeActionBarTitle(getString(R.string.title_fragment_map));
        }
        mFirestore = FirebaseFirestore.getInstance();
        mFilters = Filters.getDefault();
        if (getArguments() != null && getArguments().getSerializable(FILTER) != null) {
            mFilters = (Filters) getArguments().getSerializable(FILTER);

        }
        mFilterDialog.setFilters(mFilters);
        return mContentView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: ");
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                zoomToLocation(marker.getPosition(), 13);
                marker.showInfoWindow();
                return true;
            }
        });

        //desenhar pontos!
        addMarker(new LatLng(41.047010, -8.287442), "Casa", "Casa do paulinho na casa");

        try {
            tamega = new GeoJsonLayer(mGoogleMap, R.raw.tamegaesousa, mContext);
            GeoJsonPolygonStyle style = tamega.getDefaultPolygonStyle();
            style.setStrokeColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            style.setStrokeWidth(6f);
            tamega.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        query = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
                .orderBy(PointOfInterest.FIELD_DATE);
        mapAdapter = new MapAdapter(query, this);
        mapAdapter.startListening();
        onFilter(mFilters);
    }


    private void addMarker(LatLng latLng, String title, String content) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);
        markers.add(mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(content)
                .icon(icon)));
    }

    private void zoomToLocation(LatLng latLng, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentsCommunicationListener) {
            mContext = context;
            communicationListener = (OnFragmentsCommunicationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentsCommunicationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mapAdapter != null) {
            mapAdapter.stopListening();
        }
    }


    @Override
    public void addItemToMap(List<PointOfInterest> pointOfInterests) {
        removeAllMarkers();
        for (PointOfInterest pointOfInterest : pointOfInterests) {
            addMarker(pointOfInterest.getLocation(), pointOfInterest.getName(), pointOfInterest.getCity());
        }
    }

    @Override
    public void onFilter(Filters filters) {
        Query query = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION);

        if (filters.hasTypeOfLocation()) {
            query = query.whereEqualTo(PointOfInterest.FIELD_TYPE_OF_LOCATION, filters.getTypeOfLocation());
        }
        if (filters.hasCity()) {
            query = query.whereEqualTo(PointOfInterest.FIELD_CITY, filters.getCity());
        }
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }
        mapAdapter.setQuery(query);
        currentSearch.setText(Html.fromHtml(filters.getSearchDescription(mContext)));
        currentSortBy.setText(filters.getOrderDescription(mContext));
        this.mFilters = filters;
    }


    @Override
    public void removeAllMarkers() {
        for (Marker marker : markers) {
            marker.remove();

        }
    }

    @Override
    public void onDestroyView() {
        getArguments().putSerializable(FILTER, mFilters);
        super.onDestroyView();
    }
}

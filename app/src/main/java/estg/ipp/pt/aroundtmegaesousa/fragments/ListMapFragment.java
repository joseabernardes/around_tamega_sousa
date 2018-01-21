package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import estg.ipp.pt.aroundtmegaesousa.adapters.CustomInfoWindowAdapter;
import estg.ipp.pt.aroundtmegaesousa.adapters.MapAdapter;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsCommunicationListener;
import estg.ipp.pt.aroundtmegaesousa.models.Filters;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;


public class ListMapFragment extends Fragment implements OnMapReadyCallback, MapAdapter.onItemsChangeListener, View.OnClickListener, FilterDialogFragment.FilterListener {

    private static final String TAG = "ListMapFragment";
    private static final String ARG_FRAG_ID = "fragment_id";
    private static final int PAGE_SIZE = 10;
    public static final String FILTER = "filter";
    public static final String LIST_MAP = "list_map";
    public static final String REC_MAP = "rec_map";

    private int fragmentID;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Context mContext;
    private OnFragmentsCommunicationListener communicationListener;
    private FloatingActionButton viewMore;
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
    List<PointOfInterest> pointOfInterests;
    private int currentIndex;
    private View loadingMap;

    public ListMapFragment() {

    }


    public static ListMapFragment newInstance(String type, int fragmentID, ArrayList<PointOfInterest> pointOfInterests) {
        ListMapFragment fragment = new ListMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAG_ID, fragmentID);
        args.putSerializable("lista", pointOfInterests);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentID = getArguments().getInt(ARG_FRAG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View mContentView = inflater.inflate(R.layout.fragment_map_view, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        filterBar = mContentView.findViewById(R.id.filter_bar);
        currentSearch = mContentView.findViewById(R.id.text_current_search);
        currentSortBy = mContentView.findViewById(R.id.text_current_sort_by);
        currentSortBy.setText(" "); //não existe ordem no mapa
        buttonCancel = mContentView.findViewById(R.id.button_clear_filter);
        viewMore = mContentView.findViewById(R.id.view_more);
        loadingMap = mContentView.findViewById(R.id.map_loading_progress);
        //onClicks
        viewMore.setOnClickListener(this);
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

        mFilterDialog = FilterDialogFragment.newInstance(true);
        markers = new ArrayList<>();
        if (communicationListener != null) {
            communicationListener.changeActionBarTitle(getString(R.string.title_fragment_map), true);
            communicationListener.changeSelectedNavigationItem(fragmentID);
        }
        mFirestore = FirebaseFirestore.getInstance();
        mFilterDialog.setFilters(mFilters);
        query = mFirestore.collection(FirebaseHelper.POINTS_COLLECTION)
                .orderBy(PointOfInterest.FIELD_DATE);
        mapAdapter = new MapAdapter(query, this);


        communicationListener.showFloatingButton(false);

        return mContentView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");


        if (getArguments() != null && getArguments().getSerializable(FILTER) != null) {
            Log.d(TAG, "onResume: asARGS");
            mFilters = (Filters) getArguments().getSerializable(FILTER);
            onFilter(mFilters);
        } else {
            mFilters = Filters.getDefault();
        }

        Log.d(TAG, "onResume: ");
        super.onResume();
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
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                PointOfInterest pointOfInterest = (PointOfInterest) marker.getTag();
                Fragment fragment = PointOfInterestFragment.newInstance(pointOfInterest);
                communicationListener.replaceFragment(fragment);
            }
        });

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter((Activity) mContext);
        mGoogleMap.setInfoWindowAdapter(adapter);


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
        onFilter(mFilters);
    }


    private void addMarker(PointOfInterest pointOfInterest) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic__map_marker);
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(pointOfInterest.pointLocation())
                .title(pointOfInterest.getName())
                .icon(icon));

        marker.setTag(pointOfInterest);
        markers.add(marker);
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
        this.pointOfInterests = pointOfInterests;
        currentIndex = 0;

        if (!addMoreItemsToMap()) {
            viewMore.hide();
        } else {
            viewMore.show();
        }
        loadingMap.setVisibility(View.GONE);
    }

    /**
     * @return true se ainda tiver mais pontos para mostrar
     */
    private boolean addMoreItemsToMap() {

        boolean hasMore = true;
        int nextIndex = currentIndex + PAGE_SIZE;
        if (nextIndex > pointOfInterests.size()) {
            nextIndex = pointOfInterests.size();
            hasMore = false;
        }
        List<PointOfInterest> tempList = pointOfInterests.subList(currentIndex, nextIndex);
        for (PointOfInterest pointOfInterest : tempList) {
            addMarker(pointOfInterest);
        }
        currentIndex = nextIndex;
        return hasMore;
    }

    @Override
    public void onFilter(Filters filters) {
        loadingMap.setVisibility(View.VISIBLE);
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
   /*     currentSortBy.setText(filters.getOrderDescription(mContext));*/
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

    @Override
    public void onClick(View v) {
        if (v.getId() == viewMore.getId()) {
            if (!addMoreItemsToMap()) {
                viewMore.hide();
            }

        }
    }
}

package estg.ipp.pt.aroundtmegaesousa.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.interfaces.OnFragmentsActionBarListener;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Context mContext;
    private View filterBar;
    private View clearFilter;
    private FilterDialogFragment mFilterDialog;


    public MapFragment() {

    }


    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_map_view, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        filterBar = mContentView.findViewById(R.id.filter_bar);
        filterBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
            }
        });
        clearFilter = mContentView.findViewById(R.id.button_clear_filter);

        mFilterDialog = new FilterDialogFragment();

        if (mContext != null) {
            ((OnFragmentsActionBarListener) mContext).changeActionBarTitle(getString(R.string.title_fragment_map));
        }
        return mContentView;
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

        //desenhar pontos!
        addMarker(new LatLng(41.047010, -8.287442), "Casa", "Casa do paulinho na casa");



        /*getApi().getListOfPoints("London", "attraction", "Restaurant")
                .enqueue(new Callback<List<Point>>() {
                    @Override
                    public void onResponse(Call<List<Point>> call, Response<List<Point>> response) {
                        List<Point> points = response.body();
                        for (Point point : points) {
                            addMarker(new LatLng(point.getLat(), point.getLng()), point.getName(), point.getAddress());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Point>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Erro ao obter resposta", Toast.LENGTH_SHORT).show();
                    }
                });*/

    }


    private void addMarker(LatLng latLng, String title, String content) {
       /* LatLng latLng = new LatLng(41.36688992,-8.1947654);*/
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(content));
    }

    private void zoomToLocation(LatLng latLng, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //context instanceof OnFragmentInteractionListener &&
        if (context instanceof OnFragmentsActionBarListener) {
            mContext = context;
        } else {
          /*  throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

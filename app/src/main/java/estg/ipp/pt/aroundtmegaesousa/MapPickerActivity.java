package estg.ipp.pt.aroundtmegaesousa;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.location.LocationSettingsResponse;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.LineString;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import estg.ipp.pt.aroundtmegaesousa.utils.LocationUtils;
import estg.ipp.pt.aroundtmegaesousa.utils.MapUtils;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMapClickListener {


    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private Toolbar toolbar;
    private GeoJsonLayer tamega;
    private boolean permissionLocation;
    private boolean locationEnable;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_map_picker);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


    }


    @SuppressLint("MissingPermission")
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
        mGoogleMap.setOnMapClickListener(this);

        permissionLocation = LocationUtils.checkAndRequestPermissions(this);
        Task task = LocationUtils.enableLocationSettings(this);
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                locationEnable = true;
                if (permissionLocation) { //só se tiver permissões e a localização ativa
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }
        });
        try {
            tamega = new GeoJsonLayer(mGoogleMap, R.raw.tamegaesousa_json, this);
            googleMap.addPolyline(new PolylineOptions()
                    .addAll(MapUtils.getListFromGeoJson(tamega))
                    .color(ContextCompat.getColor(this, R.color.colorPrimaryDark)));



/*
            GeoJsonPolygonStyle style = tamega.getDefaultPolygonStyle();
           *//* style.setFillColor(ContextCompat.getColor(mContext, R.color.colorAccent));*//*
            style.setStrokeColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            style.setStrokeWidth(6f);
            tamega.addLayerToMap();*/
       /*     MapUtils.containsLocation(tamega, new LatLng(41.047010, -8.287442));*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (MapUtils.containsLocation(tamega, latLng)) {
            if (marker != null) {
                marker.remove();
            }
            marker = addMarker(latLng);
        } else {
            Toast.makeText(this, "Não percente ao Tamega e Sousa!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_point:
                //TODO
                Toast.makeText(this, "Guardar", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }


    private Marker addMarker(LatLng latLng) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);

        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(icon)
        );
        return marker;
    }

    private void zoomToLocation(LatLng latLng, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionLocation = LocationUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        if (permissionLocation) {
            if (locationEnable) { //tem permissões e tem a localização ativa
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            Toast.makeText(this, getString(R.string.warn_permission_dennied), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationEnable = LocationUtils.checkLocationSettings(requestCode, resultCode);
        if (locationEnable) {
            if (permissionLocation) {//tem permissões e tem a localização ativa
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            Toast.makeText(this, getString(R.string.warn_location_disable), Toast.LENGTH_SHORT).show();
        }
    }


}

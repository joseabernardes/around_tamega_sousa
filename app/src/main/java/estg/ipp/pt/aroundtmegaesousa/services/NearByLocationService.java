package estg.ipp.pt.aroundtmegaesousa.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.MainActivity;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.FirebaseHelper;
import estg.ipp.pt.aroundtmegaesousa.utils.PrivateNotification;

/**
 * Created by PC on 20/01/2018.
 */

public class NearByLocationService extends Service implements FirebaseHelper.FirebaseGetNerbyPointsOfInterest {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private static final int LOCATION_INTERVAL = 5000;
    public static final String LIST_POI = "LIST_POI";


    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "ONSTARTCOMAND", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals("REQUEST_LOCATION")) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(LOCATION_INTERVAL);
            mLocationRequest.setFastestInterval(LOCATION_INTERVAL);
//            Toast.makeText(this, "AQUI", Toast.LENGTH_SHORT).show();
            mLocationCallback = new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location locations = null;
                    Toast.makeText(NearByLocationService.this, "AQUI AGRORA", Toast.LENGTH_SHORT).show();
                    for (Location location : locationResult.getLocations()) {
                        locations = location;
                    }
                    if (locations != null) {
                        //locations
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                        Toast.makeText(NearByLocationService.this, String.valueOf(locations.getLatitude()) + ":" + String.valueOf(locations.getLongitude()), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NearByLocationService.this, "NULL", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            startLocationUpdates();
        }
        return START_REDELIVER_INTENT;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Toast.makeText(this, getString(R.string.message_toast_restart_service), Toast.LENGTH_SHORT).show();
        super.onTaskRemoved(rootIntent);
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "COM PERMI", Toast.LENGTH_SHORT).show();
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void getNerbyPointsOfInterest(ArrayList<PointOfInterest> pointOfInterests) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LIST_POI, pointOfInterests);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PrivateNotification resultNotification = new PrivateNotification(this, getString(R.string.recommendations), getString(R.string.recommendations_list_poi), R.drawable.around_logo, PrivateNotification.getRandomID());
        resultNotification.setAction(pi);
    }
}

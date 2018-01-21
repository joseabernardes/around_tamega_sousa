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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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

    private static final String TAG = "NearByLocationService";
    public static final String REQUEST_LOCATION = "request_location";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest.Builder builder;
    private static final int LOCATION_INTERVAL = 5000;
    public static final String LIST_POI = "LIST_POI";
    private FirebaseHelper firebaseHelper;


    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        firebaseHelper = new FirebaseHelper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent.getAction().equals(REQUEST_LOCATION)) {
            Log.d(TAG, "onStartCommand: ACTION");
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(LOCATION_INTERVAL);
            mLocationRequest.setFastestInterval(LOCATION_INTERVAL);
            mLocationCallback = new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location locations = null;
                    for (Location location : locationResult.getLocations()) {
                        locations = location;
                    }
                    if (locations != null) {
                        //locations
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                        firebaseHelper.getNearbyLocations(locations, NearByLocationService.this);
                    }
                }
            };
            startLocationUpdates();
        }
        return START_REDELIVER_INTENT;
    }


    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "startLocationUpdates: PERMITIONS");
            builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    //se tiver localizaçao ativa
                    if (ActivityCompat.checkSelfPermission(NearByLocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                    }
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    stopSelf();//MATAR O SERVICO
                }
            });

        } else {
            Log.d(TAG, "startLocationUpdates: NOPERMITIONS");
            stopSelf();//MATAR O SERVICO
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void getNerbyPointsOfInterest(ArrayList<PointOfInterest> pointOfInterests) {
        if (!pointOfInterests.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(LIST_POI, pointOfInterests);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            PrivateNotification resultNotification = new PrivateNotification(this, getString(R.string.recommendations), String.valueOf(pointOfInterests.size()) + " " + getString(R.string.recommendations_list_poi), R.drawable.around_logo, PrivateNotification.getRandomID());
            resultNotification.setAction(pi);
            resultNotification.show();
        }
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}

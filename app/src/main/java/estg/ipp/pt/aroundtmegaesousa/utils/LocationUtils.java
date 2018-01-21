package estg.ipp.pt.aroundtmegaesousa.utils;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by José Bernardes on 30/12/2017.
 */

public class LocationUtils {

    public static final int REQUEST_LOCATION = 1010;

    public static final int REQUEST_CHECK_SETTINGS = 2020;


    public static boolean checkAndRequestPermissions(Activity context, String permission) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, REQUEST_LOCATION);
        } else {
            return true;
        }
        return false;
    }

    public static boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, String permission) {

        if (requestCode == REQUEST_LOCATION) {
            if (permissions.length == 1 && permissions[0].equals(permission) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public static Task enableLocationSettings(final Activity context, int priority) {
        LocationRequest mLocationRequest;
        LocationSettingsRequest.Builder builder;
        mLocationRequest = new LocationRequest().setPriority(priority);
        builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        //vai aparecer o dialog para pedir alteração das definições
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(context,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            //ignorado por recomendação do developers.google
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

        return task;
    }

    public static boolean checkLocationSettings(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                return true;
            }
        }
        return false;
    }
}

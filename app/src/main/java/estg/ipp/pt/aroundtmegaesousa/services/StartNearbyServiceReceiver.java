package estg.ipp.pt.aroundtmegaesousa.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import estg.ipp.pt.aroundtmegaesousa.activities.SettingsActivity;

/**
 * Created by José Bernardes on 20/01/2018.
 */

public class StartNearbyServiceReceiver extends BroadcastReceiver {


    public static final String ACTION = "estg.ipp.pt.aroundtmegaesousa.startnerby";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent i = new Intent(context, NearByLocationService.class);
            i.setAction(NearByLocationService.REQUEST_LOCATION);
            context.startService(i);


        }
    }
}

package estg.ipp.pt.aroundtmegaesousa.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.Set;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.services.NearByLocationService;
import estg.ipp.pt.aroundtmegaesousa.services.PushNotificationService;
import estg.ipp.pt.aroundtmegaesousa.services.StartNearbyServiceReceiver;
import estg.ipp.pt.aroundtmegaesousa.utils.LocationUtils;
import estg.ipp.pt.aroundtmegaesousa.utils.ThemeUtils;


public class SettingsActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener {

    private Toolbar tb;
    private Spinner sp;
    private AlertDialog dialog;
    private boolean themeSettings, themeFirst;
    public static final int LIGHT_GREEN = 0;
    public static final int DARK_GREEN = 1;
    public static final int BROWN = 2;
    public static final int BLUE = 3;
    private Switch aSwitch, nearby;
    private boolean locationEnable, permissionLocation, alarmActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeSettings = false;
        themeFirst = true;
        themeSettings = getIntent().getBooleanExtra("theme", themeSettings);
        setContentView(R.layout.activity_settings);

        tb = findViewById(R.id.toolbar);
        tb.setTitle(getString(R.string.action_settings));
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        aSwitch = findViewById(R.id.id_switch);
        nearby = findViewById(R.id.switch_nearby);
        sp = findViewById(R.id.settings_spinner);
        final CheckBox checkBox_sound = findViewById(R.id.settings_sound);
        final CheckBox checkBox_vibration = findViewById(R.id.settings_vibration);


        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        int color = m.getInt("AppliedTheme", LIGHT_GREEN);
        sp.setSelection(color);

        boolean notif = m.getBoolean("sound", false);
        checkBox_sound.setChecked(notif);

        boolean vibration = m.getBoolean("vibration", false);
        checkBox_vibration.setChecked(vibration);

        boolean push = m.getBoolean("push", true);
        aSwitch.setChecked(push);

        aSwitch.setText(getString(R.string.active));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor mEditor = mSettings.edit();
                mEditor.putBoolean("push", isChecked);
                mEditor.apply();
                if (isChecked) {
                    aSwitch.setText(getString(R.string.active));
                    FirebaseMessaging.getInstance().subscribeToTopic(PushNotificationService.TOPIC);
                } else {
                    aSwitch.setText(getString(R.string.inaactive));
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(PushNotificationService.TOPIC);
                }
            }
        });

        boolean nerb = m.getBoolean("nearby", true);
        alarmActivated = nerb;
        nearby.setChecked(nerb);
        if (nerb) {
            nearby.setText(getString(R.string.active));
        } else {
            nearby.setText(getString(R.string.inaactive));
        }
        nearby.setOnCheckedChangeListener(this);


        checkBox_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor mEditor = mSettings.edit();
                mEditor.putBoolean("sound", isChecked);
                mEditor.apply();
            }
        });

        checkBox_vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor mEditor = mSettings.edit();
                mEditor.putBoolean("vibration", isChecked);
                mEditor.apply();
            }
        });


        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!themeFirst) {
                    SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor mEditor = mSettings.edit();
                    mEditor.putInt("AppliedTheme", position);
                    mEditor.apply();
                    themeSettings = true;
                    finish();
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    intent.putExtra("theme", themeSettings);
                    startActivity(intent);
                } else {
                    themeFirst = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.pre_settings));
        builder.setTitle(getString(R.string.predefinitions));

        builder.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                sp.setSelection(0);
                checkBox_sound.setChecked(false);
                checkBox_vibration.setChecked(false);
                aSwitch.setChecked(true);
                FirebaseMessaging.getInstance().subscribeToTopic(PushNotificationService.TOPIC);
                finish();
                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                intent.putExtra("theme", themeSettings);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.nao), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        dialog = builder.create();


    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            activateNearby();
        } else {
            setNearbyPreference(false);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(SettingsActivity.this, StartNearbyServiceReceiver.class);
            intent.setAction(StartNearbyServiceReceiver.ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmActivated = false;
            nearby.setText(getString(R.string.active));
            alarmManager.cancel(pendingIntent);
        }
    }

    private void setCheckedNearby(boolean check) {
        nearby.setOnCheckedChangeListener(null);
        nearby.setChecked(check);
        setNearbyPreference(check);
        if (check) {
            nearby.setText(getString(R.string.active));
            setAlarmNearby();
        } else {
            nearby.setText(getString(R.string.inaactive));
        }
        nearby.setOnCheckedChangeListener(this);
    }


    public void activateNearby() {
        permissionLocation = LocationUtils.checkAndRequestPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        Task task = LocationUtils.enableLocationSettings(this, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                locationEnable = true;
                if (permissionLocation && !alarmActivated) { //só se tiver permissões e a localização ativa
                    alarmActivated = true;
                    setCheckedNearby(true);
                } else {
                    setCheckedNearby(false);
                }
            }
        });

    }


    private void setNearbyPreference(boolean pref) {
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor mEditor = mSettings.edit();
        mEditor.putBoolean("nearby", pref);
        mEditor.apply();

    }

    private void setAlarmNearby() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StartNearbyServiceReceiver.class);
        intent.setAction(StartNearbyServiceReceiver.ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (36000), 3600000, pendingIntent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionLocation = LocationUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionLocation) {
            if (locationEnable && !alarmActivated) { //tem permissões e tem a localização ativa
                alarmActivated = true;
                setCheckedNearby(true);
            }
        } else {
            setCheckedNearby(false);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationEnable = LocationUtils.checkLocationSettings(requestCode, resultCode);
        if (locationEnable) {
            if (permissionLocation && !alarmActivated) {//tem permissões e tem a localização ativa
                alarmActivated = true;
                setCheckedNearby(true);
            }
        } else {
            setCheckedNearby(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (themeSettings) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }

    }

}

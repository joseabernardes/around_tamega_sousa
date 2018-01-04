package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import estg.ipp.pt.aroundtmegaesousa.R;


public class SettingsActivity extends AppCompatActivity {

    private Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        tb = findViewById(R.id.toolbar);
        tb.setTitle("Settings");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        CheckBox checkBox_sound = findViewById(R.id.settings_sound);
        CheckBox checkBox_vibration = findViewById(R.id.settings_vibration);

        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notif = m.getBoolean("sound", true);
        checkBox_sound.setChecked(notif);

        boolean vibration = m.getBoolean("vibration", true);
        checkBox_vibration.setChecked(vibration);

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

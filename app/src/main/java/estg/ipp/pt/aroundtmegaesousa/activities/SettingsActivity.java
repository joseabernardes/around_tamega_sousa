package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import estg.ipp.pt.aroundtmegaesousa.R;


public class SettingsActivity extends AppCompatActivity {

    private Toolbar tb;
    private Spinner sp;
    public static final int LIGHT_GREEN = 0;
    public static final int DARK_GREEN = 1;
    public static final int BROWN = 2;
    public static final int BLUE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = m.getInt("AppliedTheme", SettingsActivity.LIGHT_GREEN);
        if (theme == SettingsActivity.LIGHT_GREEN) {

            setTheme(R.style.AppTheme);
        } else if (theme == SettingsActivity.DARK_GREEN) {
            setTheme(R.style.AppTheme_Secondary);
        } else if (theme == SettingsActivity.BROWN) {
            setTheme(R.style.AppTheme_Brown);
        } else if (theme == SettingsActivity.BLUE) {
            setTheme(R.style.AppTheme_Blue);
        }
        setContentView(R.layout.activity_settings);


        tb = findViewById(R.id.toolbar);
        tb.setTitle("Settings");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sp = findViewById(R.id.settings_spinner);
        CheckBox checkBox_sound = findViewById(R.id.settings_sound);
        CheckBox checkBox_vibration = findViewById(R.id.settings_vibration);


        int color = m.getInt("AppliedTheme", LIGHT_GREEN);
        sp.setSelection(color);


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

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor mEditor = mSettings.edit();
                mEditor.putInt("AppliedTheme", position);
                mEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


//        switch (text) {
//
//
//            case "Verde Claro":
//                mEditor.putInt("AppliedTheme",LIGHT_GREEN);
//                mEditor.apply();
//                sp.setSelection(2);
//
//                break;
//            case "Verde Escuro":
//                mEditor.putInt("AppliedTheme_secondary",DARK_GREEN);
//                mEditor.apply();
//                sp.setSelection(3);
//                break;
//        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

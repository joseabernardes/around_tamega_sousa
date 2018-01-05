package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.SettingsActivity;


public class ThemeUtils {

    public static final String THEME = "AppliedTheme";

    public static void changeTheme(Activity context) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(context);
        int theme = m.getInt(THEME, SettingsActivity.LIGHT_GREEN);
        switch (theme) {
            case SettingsActivity.LIGHT_GREEN:
                context.setTheme(R.style.AppTheme);
                break;
            case SettingsActivity.DARK_GREEN:
                context.setTheme(R.style.AppTheme_Secondary);
                break;
            case SettingsActivity.BROWN:
                context.setTheme(R.style.AppTheme_Brown);
                break;
        }
    }
}

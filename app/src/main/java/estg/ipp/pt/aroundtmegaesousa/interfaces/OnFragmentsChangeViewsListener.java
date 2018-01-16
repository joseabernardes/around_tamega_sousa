package estg.ipp.pt.aroundtmegaesousa.interfaces;

import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseUser;

import estg.ipp.pt.aroundtmegaesousa.activities.MainActivity;

/**
 * Created by José Bernardes on 29/12/2017.
 */

public interface OnFragmentsChangeViewsListener {
    void changeActionBarTitle(String title);

    void showFloatingButton(boolean state);

    boolean isShownFloatingButton();

    void replaceFragment(Fragment fragment);

    FirebaseUser getLoggedUser();

    void setOnBackPressedListener(MainActivity.OnBackPressedListener onBackPressedListener);

    void removeOnBackPressedListener();
}

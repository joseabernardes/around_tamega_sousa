package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import estg.ipp.pt.aroundtmegaesousa.R;

/**
 * Created by José Bernardes on 09/01/2018.
 */

public class BaseActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private String TAG = "BaseActivity";
    public static final int RC_SIGN_IN = 1;
    protected FirebaseAuth mFirebaseAuth;
    protected FirebaseUser user;

    private void onSignInInitialize(FirebaseUser user) {
        if (this instanceof MainActivity) {
            MainActivity mainActivity = ((MainActivity) this);
            mainActivity.addUserInfo(user);
        }
    }

    private void onSignOutCleanup() {

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.d(TAG, "onAuthState");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {    //signed in
            onSignInInitialize(user);
        } else { //nao tem login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(this);
    }

}

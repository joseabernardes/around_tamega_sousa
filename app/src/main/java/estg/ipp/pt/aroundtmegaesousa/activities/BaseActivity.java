package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.utils.FirestoreHelper;
import estg.ipp.pt.aroundtmegaesousa.utils.NotificationUtils;

/**
 * Created by José Bernardes on 09/01/2018.
 */

public class BaseActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, FirestoreHelper.FirestoreCommunication {

    private String TAG = "BaseActivity";
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
            this.user = user;
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

    @Override
    public void addPointResult(boolean result, final String documentID, int resultCode, NotificationUtils nt) {

        if (result) {
            nt.updateStatus(001,100);
            nt.finalNotify(001,"Adicionado");
        } else {
            String message = getString(R.string.message_snackbar_not_added);
            if (resultCode == FirestoreHelper.RESULT_FAIL_ADD_DATABASE) {
                message = getString(R.string.message_snackbar_not_added_database);
            } else if (resultCode == FirestoreHelper.RESULT_FAIL_UPLOAD_IMAGES) {
                message = getString(R.string.message_snackbar_not_added_upload);
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}

package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import estg.ipp.pt.aroundtmegaesousa.R;

public class LoginActivity extends AppCompatActivity{

    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private String TAG = "LoginActivity";
    private TextView errorMessage;
    private Button button;


    private void initLayout(String message) {
        setContentView(R.layout.activity_login);
        errorMessage = findViewById(R.id.error_message);
        errorMessage.setText(message);
        button = findViewById(R.id.try_again);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) { //signed in
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {//nao tem login
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                    ))
                            .setLogo(R.drawable.around_logo)
                            .setTheme(R.style.LoginTheme)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult Req:" + requestCode + " Res:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) { //se foi do meu pedido de login
            if (resultCode == RESULT_OK) { //se retornou sucesso
                FirebaseUser user = mFirebaseAuth.getCurrentUser();

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) { // se o utilizador cancelou BACK ou sem ligação á internet
                IdpResponse response = IdpResponse.fromResultIntent(data);
                // Sign in failed
                String toast = getString(R.string.warn_login_unknown_response);
                if (response == null) {
                    finish();
                    return;
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    toast = getString(R.string.warn_login_no_connection);
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    toast = getString(R.string.warn_login_unknown_error);
                }
                initLayout(toast);
            }
        }
    }
/*
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.d(TAG, "onAuthStateChanged: ");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) { //signed in
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {//nao tem login
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                    ))
                            .setLogo(R.drawable.around_logo)
                            .setTheme(R.style.LoginTheme)
                            .build(),
                    RC_SIGN_IN);
        }
    }
*/

}
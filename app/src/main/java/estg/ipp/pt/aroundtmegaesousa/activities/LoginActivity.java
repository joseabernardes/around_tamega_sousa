package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import estg.ipp.pt.aroundtmegaesousa.R;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private String TAG = "LoginActivity";
    private TextView errorMessage;
    private Button button;
    protected FirebaseUser user;

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
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            Log.d(TAG, "onActivityResult Req:" + requestCode + " Res:" + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) { //se foi do meu pedido de login
                if (resultCode == RESULT_OK) { //se retornou sucesso

                    String url = mFirebaseAuth.getCurrentUser().getPhotoUrl().toString();
                    String name = mFirebaseAuth.getCurrentUser().getUid();
                    new DownloadImage().execute(url, name);

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


    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }


    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private String filename;

        private Bitmap downloadImageBitmap(String sUrl, String name) {
            filename = name;
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadImageBitmap(strings[0], strings[1]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getApplicationContext(), result, filename);

        }


    }

}



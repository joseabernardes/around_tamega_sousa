package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.models.Quotes;

public class TestActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1;
    private EditText editText1;
    private EditText editText2;
    private Button save;
    private Button fetch;
    private TextView quote;
    private FirebaseFirestore db;
    private DocumentReference mDocRef;
    private String TAG = "test";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TextView loginName;
    private Button signOut;
    private LinearLayout linearLayout;

    //perguntar ao professor
    private void initLayout() {
        setContentView(R.layout.activity_test);
        linearLayout = findViewById(R.id.linear);
        loginName = findViewById(R.id.login_name);
        mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");
        fetch = findViewById(R.id.fetch);
        quote = findViewById(R.id.quote);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        save = findViewById(R.id.save);
        signOut = findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(TestActivity.this);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "ENTROU", Toast.LENGTH_SHORT).show();
                String quote = editText1.getText().toString();
                String author = editText2.getText().toString();
                if (!quote.isEmpty() && !author.isEmpty()) {
  /*                Map<String, Object> data = new HashMap<String, Object>();
                    data.put(QUOTE, quote);
                    data.put(AUTHOR, author);*/
                  /*  db.collection("users").add(new Quotes(quote, author)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                DocumentReference documentReference = task.getResult();
                                String id = documentReference.getId();
                                Toast.makeText(TestActivity.this, "ID: " + id, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/
                }
            }
        });
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference col = db.collection("users");
                DocumentReference documentReference = col.document("IcSBvgsxTMzXpXsi43st");
/*
                DocumentSnapshot documentSnapshot = documentReference.get().getResult();
*/
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Log.d(TAG, documentSnapshot.getId() + " - " + documentSnapshot.getData());
                        Quotes quotes = documentSnapshot.toObject(Quotes.class);
                        Log.d(TAG, quotes.toString());
                    }
                });


/*
                Query query = col.whereEqualTo("author", "paulo");
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d(TAG, documentSnapshot.getId() + " - " + documentSnapshot.getData());
                            }

                        }
                    }
                });*/

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthStateListener = new AuthStateListener();
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }


    private void onSignInInitialize(String string) {
        loginName.setText(string);
    }

    private void onSignOutCleanup() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) { //se foi do meu pedido de login
            if (resultCode == RESULT_OK) { //se retornou sucesso
                initLayout();
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) { // se o utilizador cancelou BACK ou sem ligação á internet
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class AuthStateListener implements FirebaseAuth.AuthStateListener {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //signed in
                initLayout();
                onSignInInitialize(user.getDisplayName());
            } else {
                //signed out
                onSignOutCleanup();
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
    }


    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}

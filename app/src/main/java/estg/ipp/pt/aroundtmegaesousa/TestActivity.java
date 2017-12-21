package estg.ipp.pt.aroundtmegaesousa;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import estg.ipp.pt.aroundtmegaesousa.model.Quotes;

public class TestActivity extends AppCompatActivity {
    public static final String QUOTE = "quote";
    public static final String AUTHOR = "author";
    private EditText editText1;
    private EditText editText2;
    private Button save;
    private Button fetch;
    private TextView quote;
    private FirebaseFirestore db;
    private DocumentReference mDocRef;
    private String TAG = "test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        db = FirebaseFirestore.getInstance();


        mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");
        fetch = findViewById(R.id.fetch);
        quote = findViewById(R.id.quote);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        save = findViewById(R.id.save);
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
                    db.collection("users").add(new Quotes(quote, author)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                DocumentReference documentReference = task.getResult();
                                String id = documentReference.getId();
                                Toast.makeText(TestActivity.this, "ID: " + id, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference col = db.collection("users");

                Query query = col.whereEqualTo("author", "paulo");
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                Log.d(TAG, documentSnapshot.getId() + " - " + documentSnapshot.getData());
                            }

                        }
                    }
                });



            /*    mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                               *//* String quote = documentSnapshot.getString(QUOTE);
                                String author = documentSnapshot.getString(AUTHOR);*//*
                                Quotes quotes = documentSnapshot.toObject(Quotes.class);
                                quote.setText("\"" + quotes.getQuote() + "\" -- " + quotes.getAuthor());


                            }
                        }
                    }
                });*/
            }
        });

    }


}

package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import estg.ipp.pt.aroundtmegaesousa.R;

public class RandomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        TextView tv = findViewById(R.id.random_text_view);
        Intent intent = getIntent();
        String x = intent.getExtras().getString("documentID");
        tv.setText(x);
    }
}

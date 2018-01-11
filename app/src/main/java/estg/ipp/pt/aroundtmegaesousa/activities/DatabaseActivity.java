package estg.ipp.pt.aroundtmegaesousa.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.utils.DbHelper;

public class DatabaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);


    }

    public void insertPoi(PointOfInterest poi, String type) throws Exception {
        DbHelper dbH = new DbHelper(this);
        SQLiteDatabase db = dbH.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", poi.getName());
        values.put("description", poi.getDescription());
        values.put("rating", poi.getAvgRatting());
        values.put("typeofLocal", poi.getTypeOfLocation());
        values.put("latitude", poi.getLocation().getLatitude());
        values.put("longitude", poi.getLocation().getLongitude());
        values.put("typeOfPoi", type);


        long row = db.insert("poi", null, values);
        if (row < 0) {
            throw new Exception("Não foi possível inserir!");
        }
        db.close();
    }

    public void deletePoi(PointOfInterest p) throws Exception {
        DbHelper dbH = new DbHelper(this);
        SQLiteDatabase db = dbH.getWritableDatabase();
        long row = db.delete("poi", "id =" + p.getId(), null);
        long row2 = db.delete("photo", "id_poi=" + p.getId(), null);
        if(row < 0 && row2 <0){
            throw new Exception("Não foi possível inserir!");
        }
        db.close();
    }
}

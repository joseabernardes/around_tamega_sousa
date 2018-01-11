package estg.ipp.pt.aroundtmegaesousa.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PC on 11/01/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aplication.db";
    private static final int DATABASE_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE poi(id INTEGER PRIMARY KEY AUTOINCREMENT , title VARCHAR(25)NOT NULL,description VARCHAR(75)NOT NULL,rating REAL NOT NULL,typeofLocal VARCHAR(25)NOT NULL, latitude REAL NOT NULL, longitude REAL NOT NULL,typeOfPoi VARCHAR(15) NOT NULL)");
        db.execSQL("CREATE TABLE photo(id INTEGER PRIMARY KEY AUTOINCREMENT, photo VARCHAR(100)NOT NULL, id_poi INTEGER, FOREIGN KEY(id_poi) REFERENCES poi(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}

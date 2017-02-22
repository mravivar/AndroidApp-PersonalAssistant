package mcgroup10.com.batroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Batroid.db";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    //Insert geofences in table
    public boolean insertData(String t_name, String name, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("LATITUDE", latitude);
        contentValues.put("LONGITUDE", longitude);
        long result = db.insert(t_name, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    //Method to create table in database
    public void createTable(String t_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + t_name + " ( NAME VARCHAR2(10) PRIMARY KEY, LATITUDE VARCHAR2(10), LONGITUDE VARCHAR2(10))");
    }

    //Method to retrieve geofences
    public Cursor getAllData(String t_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + t_name, null);
        //res.close();
        return res;
    }

    //Method to delete geofences
    public void deleteGeofence(String t_name, String g_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + t_name + " WHERE NAME = '" + g_name + "'");
    }

    public int getRowCount(String t_name) {
        String countQuery = "SELECT  * FROM " + t_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
}

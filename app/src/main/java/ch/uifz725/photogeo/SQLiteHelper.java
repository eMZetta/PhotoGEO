package ch.uifz725.photogeo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by eMZetta March 2019.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("CREATE TABLE IF NOT EXISTS PICTURES(Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, location VARCHAR, image BLOB)");
    }

    public void insertData(String name, String location, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO PICTURES VALUES (NULL, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, location);
        statement.bindBlob(3, image);

        statement.executeInsert();
    }


    public  void deleteData(int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM PICTURES WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

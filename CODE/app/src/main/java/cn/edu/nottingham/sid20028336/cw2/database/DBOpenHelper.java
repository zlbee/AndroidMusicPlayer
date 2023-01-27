package cn.edu.nottingham.sid20028336.cw2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import cn.edu.nottingham.sid20028336.cw2.database.DBContract;

public class DBOpenHelper extends SQLiteOpenHelper {
    private final String TAG = this.getClass().getName();

    public DBOpenHelper(@Nullable Context context) {
        super(context, "MusicDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create local playlist (default playlist)
        String sql = "CREATE TABLE table_localplaylist(_id integer PRIMARY KEY AUTOINCREMENT," +
                "musicName varchar(128)," +
                "artist varchar(128)," +
                "url varchar(128) UNIQUE," +
                "duration integer)";
        db.execSQL(sql);
        DBContract.getInstance().addTableName("LocalPlaylist");
        Log.i(TAG, "DB and default table created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

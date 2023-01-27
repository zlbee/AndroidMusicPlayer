package cn.edu.nottingham.sid20028336.cw2.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper {
    DBOpenHelper DBOpenHelper;

    public DBHelper(Context context) {
        super();

        this.DBOpenHelper = new DBOpenHelper(context);
    }

    // insert an item to a table
    public void insertItemToTable(@Nullable String tableName, ContentValues values) {
        SQLiteDatabase db = DBOpenHelper.getWritableDatabase();
        tableName = tableName.toLowerCase();

        try {
            long id = db.insert(DBContract.TABLE_PREFIX + tableName, null, values);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    // count row numbers of a table
    public int countTableRowNumber(@Nullable String tableName) {
        SQLiteDatabase db = DBOpenHelper.getReadableDatabase();
        tableName = tableName.toLowerCase();

        String sql = "SELECT COUNT(*) FROM "+ DBContract.TABLE_PREFIX + tableName;
        int count = Integer.parseInt(db.compileStatement(sql).simpleQueryForString());
        return count;
    }

    // query the table in the database according to its index
    public Cursor queryTableByIndex(int index) {
        SQLiteDatabase db = DBOpenHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name != 'android_metadata' AND name != 'sqlite_sequence'", null);
        
        for (int i = 0; i < index; i++) {
            if (!c.moveToNext()) {
                break;
            }
        }
        return c;
    }

    // delete the table in the database according to its name
    public void deleteTableByTableName(String tableName) {
        SQLiteDatabase db = DBOpenHelper.getWritableDatabase();
        tableName = tableName.toLowerCase();

        String sql = "DROP TABLE "+ DBContract.TABLE_PREFIX + tableName;
        db.execSQL(sql);
    }

    // count the number of tables
    public int countTableNumber() {
        SQLiteDatabase db = DBOpenHelper.getReadableDatabase();

        String sql = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'";
        int count = Integer.parseInt(db.compileStatement(sql).simpleQueryForString());
        return count;
    }

    // create a new table
    public boolean createNewTable(String tableName) {
        SQLiteDatabase db = DBOpenHelper.getWritableDatabase();
        tableName = tableName.toLowerCase();

        if (tableName.length() == 0) {
            return false;
        }

        // check if table already exists
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name = '"+ DBContract.TABLE_PREFIX + tableName +"'", null);

        if (c.moveToNext()) {
            int count = c.getInt(0);
            if (count > 0) {
                // table exists -> return negative response
                return false;
            }
            else {
                // table not exists -> create new playlist and return positive response
                String sql = "CREATE TABLE "+ DBContract.TABLE_PREFIX + tableName +"(_id integer PRIMARY KEY AUTOINCREMENT," +
                        "musicName varchar(128)," +
                        "artist varchar(128)," +
                        "url varchar(128) UNIQUE," +
                        "duration integer)";
                db.execSQL(sql);
                DBContract.getInstance().addTableName(tableName);
                return true;
            }
        }
        return false;
    }

    // delete an item from a table according to url
    public void deleteItemFromTableByUrl(@Nullable String tableName, String url) {
        SQLiteDatabase db = DBOpenHelper.getWritableDatabase();
        tableName = tableName.toLowerCase();

        String sql = "DELETE FROM "+ DBContract.TABLE_PREFIX + tableName + " WHERE " + DBContract.URL + " == '" + url + "'";
        db.execSQL(sql);
    }

    // query an item from a table according to index
    public Cursor queryItemFromTableByIndex(@Nullable String tableName, int index) {
        SQLiteDatabase db = DBOpenHelper.getWritableDatabase();
        tableName = tableName.toLowerCase();

        int _ID = query_IdFromTableByIndex(tableName, index);
        Cursor c = db.query(DBContract.TABLE_PREFIX + tableName, null, DBContract._ID + " == " + _ID, null, null, null, null);
        return c;
    }

    // query a table for _id according to index
    private int query_IdFromTableByIndex(@Nullable String tableName, int index) {
        SQLiteDatabase db = DBOpenHelper.getReadableDatabase();
        tableName = tableName.toLowerCase();

        int _ID = 1;
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT *, (SELECT COUNT(*) FROM "+ DBContract.TABLE_PREFIX + tableName + " b WHERE a._id >= b._id) AS cnt FROM "+ DBContract.TABLE_PREFIX + tableName + " a", null);
        if (c.moveToNext()) {
            int ctn = c.getInt(5);
            do {
                if (c.getInt(5) == index + 1) {
                    _ID = c.getInt(0);
                }
            } while(c.moveToNext());
        }
        return _ID;
    }

    // query all items from table
    public Cursor queryAllItemsFromTable(@Nullable String tableName) {
        SQLiteDatabase db = DBOpenHelper.getReadableDatabase();
        tableName = tableName.toLowerCase();

        Cursor c = db.rawQuery("SELECT * FROM "+ DBContract.TABLE_PREFIX + tableName, null);
        return c;
    }

}

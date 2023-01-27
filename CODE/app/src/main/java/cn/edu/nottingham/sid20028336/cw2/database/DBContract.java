package cn.edu.nottingham.sid20028336.cw2.database;

import java.util.ArrayList;

public class DBContract {
    private  static DBContract instance = null;

    public static final String _ID = "_id";
    public static final String MUSICNAME = "musicName";
    public static final String ARTIST = "artist";
    public static final String URL = "url";
    public static final String DURATION = "duration";
    public static final String TABLE_PREFIX = "table_"; // table name should be playlist name with prefix

    public static ArrayList<String> tableNames = null;

    private DBContract() {
        super();

        tableNames = new ArrayList<String>();
    }

    public static DBContract getInstance() {
        if (instance == null) {
            instance = new DBContract();
        }
        return instance;
    }

    public void addTableName(String tableName) {
        tableNames.add(tableName);
    }
}

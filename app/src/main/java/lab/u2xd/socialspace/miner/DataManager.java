package lab.u2xd.socialspace.miner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by ysb on 2015-09-30.
 */
public class DataManager extends SQLiteOpenHelper implements BaseColumns {

    public static final String NAME_DATABASE = "ContextDatabase";
    public static final String NAME_TABLE = "ContextData";
    public static final int VERSION_DATABASE = 1;

    public static final String FIELD_TYPE = "Type";
    public static final String FIELD_GENERATOR = "Generator";
    public static final String FIELD_TARGET = "Target";
    public static final String FIELD_TIME = "Time";
    public static final String FIELD_CONTENT = "Content";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_TABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_TYPE + " TEXT, " +
            FIELD_GENERATOR + " TEXT, " +
            FIELD_TARGET + " TEXT, " +
            FIELD_TIME + " INTEGER, " +
            FIELD_CONTENT + " TEXT)";
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + NAME_TABLE;
    private static final String SQL_GET_ALL = "SELECT * FROM " + NAME_TABLE;

    private SQLiteDatabase database;

    public DataManager(Context context) {
        this(context, NAME_DATABASE, null, VERSION_DATABASE);
    }

    private DataManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //// TODO: 2015-09-30 You should find way to open database
        Log.e("Data Manager", "I am created! " + database.isOpen());
        database.execSQL(SQL_CREATE_TABLE);
        insertData("General", "Me", "Me", 0, "Success!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "I am recreated!");
        database.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "I am recreated to old version");
        super.onDowngrade(db, oldVersion, newVersion);
        database.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.e("Data Manager", "I am opened.");
    }

    public void setStatusBarNotification(StatusBarNotification sbn) {
        Log.e("Data Manager", "I got a notification data.");

        //TODO 넣어야 할 데이터 수정 할 것
        insertData(sbn.getPackageName(), sbn.getNotification().extras.getString(NotificationMiner.EXTRA_TITLE),
                "Me", System.currentTimeMillis(), sbn.getNotification().extras.getString(NotificationMiner.EXTRA_TEXT));
    }

    public String showAllData() {
        String str = "";
        Cursor cursor = database.rawQuery(SQL_GET_ALL, null);

        while(cursor.moveToNext()) {
            str += cursor.getInt(0) + " : Type " + cursor.getString(1) + ", Generator = " + cursor.getString(2) +
                    ",\r\nTime = " + cursor.getInt(4) +
                    ",\r\nContent = " + cursor.getString(5) + "\r\n \r\n";
        }

        return str;
    }

    private void insertData(String type, String generator, String target, long time, String content) {
        ContentValues values = new ContentValues();
        values.put(FIELD_TYPE, type);
        values.put(FIELD_GENERATOR, generator);
        values.put(FIELD_TARGET, target);
        values.put(FIELD_TIME, time);
        values.put(FIELD_CONTENT, content);

        long newid = 0;
        newid = database.insert(NAME_TABLE, null, values);
        if(newid != 0) {
            Log.e("Data Manager", "I save data in " + newid);
        } else {
            Log.e("Data Manager", "I failed to save data");
        }
    }


}

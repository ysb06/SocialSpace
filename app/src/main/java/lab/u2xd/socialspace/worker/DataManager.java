package lab.u2xd.socialspace.worker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lab.u2xd.socialspace.worker.object.RefinedData;

/**
 * Created by ysb on 2015-09-30.
 */
public class DataManager extends SQLiteOpenHelper implements BaseColumns {

    public static final String NAME_DATABASE = "ContextDatabase";
    public static final String NAME_TABLE = "ContextData";
    public static final int VERSION_DATABASE = 9;

    public static final String FIELD_TYPE = "Type";
    public static final String FIELD_AGENT = "Agent";
    public static final String FIELD_TARGET = "Target";
    public static final String FIELD_TIME = "Time";
    public static final String FIELD_CONTENT = "Content";

    public static final String TYPE_KAKAOTALK = "KakaoTalk";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_TABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_TYPE + " TEXT, " +
            FIELD_AGENT + " TEXT, " +
            FIELD_TARGET + " TEXT, " +
            FIELD_TIME + " INTEGER, " +
            FIELD_CONTENT + " TEXT)";
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + NAME_TABLE;
    private static final String SQL_GET_ALL = "SELECT * FROM " + NAME_TABLE;

    private static final String FILENAME_CSV = "CurrentDatabase.csv";

    public DataManager(Context context) {
        this(context, NAME_DATABASE, null, VERSION_DATABASE);
    }

    private DataManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("Data Manager", "I am making database! " + db.isOpen());
        db.execSQL(SQL_CREATE_TABLE);
        insertData(db, "General", "Me", "Me", 0, "Success!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "I am making database, again!");
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "I am making old database, again!");
        super.onDowngrade(db, oldVersion, newVersion);
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.e("Data Manager", "Here is something to work!");
    }

    @Deprecated
    public void setStatusBarNotification(StatusBarNotification sbn) {
        Log.e("Data Manager", "I got a notification data.");

        //TODO 넣어야 할 데이터 수정 할 것
        insertData(getWritableDatabase(), sbn.getPackageName(), sbn.getNotification().extras.getString(MinerManager.EXTRA_TITLE),
                "Me", System.currentTimeMillis(), sbn.getNotification().extras.getString(MinerManager.EXTRA_TEXT));
        close();
    }

    public void setRefinedData(RefinedData data) {
        insertData(getWritableDatabase(), data);
    }

    public String showAllData() {
        String str = "";
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALL, null);

        while(cursor.moveToNext()) {
            str += cursor.getInt(0) + " : " + cursor.getString(1) + ", Agent = " + cursor.getString(2) +
                    ", Time = " + cursor.getInt(4) +
                    ", Content = " + cursor.getString(5) + "\r\n \r\n";
        }
        return str;
    }

    public void reset() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DROP_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    private void insertData(SQLiteDatabase db, RefinedData data) {
        insertData(db, data.Type, data.Agent, data.Target, data.Time, data.Content);
    }

    private void insertData(SQLiteDatabase db, String type, String agent, String target, long time, String content) {
        ContentValues values = new ContentValues();
        values.put(FIELD_TYPE, type);
        values.put(FIELD_AGENT, agent);
        values.put(FIELD_TARGET, target);
        values.put(FIELD_TIME, time);
        values.put(FIELD_CONTENT, content);

        long newid = 0;
        newid = db.insert(NAME_TABLE, null, values);
        if(newid != 0) {
            Log.e("Data Manager", "I save data in " + newid);
        } else {
            Log.e("Data Manager", "I failed to save data");
        }
    }

    public void exportDatabase() {
        String str = "ID,Type,Agent,Target,Time,Content\r\n";
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALL, null);
        while(cursor.moveToNext()) {
            str += cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3) + ","
                    + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(cursor.getLong(4))) + "," + cursor.getString(5) + "\r\n";
        }
        writeNew(str);
    }

    private boolean write(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String sSDdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Context/";
            File dir = new File(sSDdir);
            if(dir.mkdir()) {
                Log.e("Data Manager", "I made an directory");
            }

            File file = new File(sSDdir, FILENAME_CSV);
            try {
                if(!file.exists()) {
                    file.createNewFile();
                    Log.e("Data Manager", "File is created");
                }
                String path = file.getAbsolutePath();
                Log.e("Data Manager", "Reading... " + path);

                FileWriter writer = new FileWriter(file, true);
                writer.append(str + "\r\n");
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            Log.e("Data Manager", "There is no storage");
            return false;
        }
    }

    private boolean writeNew(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String sSDdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Context/";
            File dir = new File(sSDdir);
            if(dir.mkdir()) {
                Log.e("Data Manager", "I made an directory");
            }

            File file = new File(sSDdir, FILENAME_CSV);
            try {
                if(file.exists()) {
                    if(file.delete())
                        Log.e("Data Manager", "File is deleted");
                    else
                        Log.e("Data Manager", "Fail to delete file");
                }
                file.createNewFile();
                String path = file.getAbsolutePath();
                Log.e("Data Manager", "Reading... " + path);

                FileWriter writer = new FileWriter(file, true);
                writer.write(str + "\r\n");
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            Log.e("Data Manager", "There is no storage");
            return false;
        }
    }
}

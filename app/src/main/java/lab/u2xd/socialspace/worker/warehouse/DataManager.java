package lab.u2xd.socialspace.worker.warehouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import lab.u2xd.socialspace.worker.object.RefinedData;

/**
 * Created by ysb on 2015-09-30.
 */
public class DataManager extends SQLiteOpenHelper implements BaseColumns {

    private static DataManager object;

    public static final String NAME_DATABASE = "ContextDatabase";
    public static final String NAME_TABLE = "ContextData";
    public static final int VERSION_DATABASE = 10;

    public static final String FIELD_TYPE = "Type";
    public static final String FIELD_AGENT = "Agent";
    public static final String FIELD_TARGET = "Target";
    public static final String FIELD_TIME = "Time";
    public static final String FIELD_CONTENT = "Content";

    public static final String CONTEXT_TYPE_KAKAOTALK = "KakaoTalk";
    public static final String CONTEXT_TYPE_CALL = "Call";
    public static final String CONTEXT_TYPE_SMS = "SMS";
    public static final String CONTEXT_TYPE_MMS = "MMS";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + NAME_TABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_TYPE + " TEXT, " +
            FIELD_AGENT + " TEXT, " +
            FIELD_TARGET + " TEXT, " +
            FIELD_TIME + " INTEGER, " +
            FIELD_CONTENT + " TEXT)";
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + NAME_TABLE;
    private static final String SQL_GET_ALL = "SELECT * FROM " + NAME_TABLE;

    private static final String FILENAME_CSV = "CurrentDatabase.csv";

    //---------------------------------------------------------------------------------------------//
    private Queue<QueryRequest> listRequest;
    private boolean isWorking = false;

    private ArrayList<Queryable> listCallback;
    //---------------------------------------------------------------------------------------------//

    private DataManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        getWritableDatabase();
        listRequest = new LinkedList<>();
        listCallback = new ArrayList<>();
    }

    public static DataManager getManager(Context context) {
        if(object == null) {
            object = new DataManager(context.getApplicationContext(), NAME_DATABASE, null, VERSION_DATABASE);
        }
        return object;
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

    //---------------------------------------------------------------------------------------------//
    private void wake() {
        if(isWorking) {
            Log.e("Data Manager","I am already awaken!");
        } else {
            Log.e("Data Manager","Whoa! Ok, Ok, I am working.");
            isWorking = true;
            Thread work = new Thread(new Runnable() {
                @Override
                public void run() {
                    work();
                    if(listCallback.size() > 0) {
                        for(int i = 0; i < listCallback.size(); i++) {
                            listCallback.get(i).onFinish_Query();
                        }
                    }
                }
            });
            work.start();
        }
    }

    // TODO: 2015-10-05 전화 및 문자 메시지 쿼리 완성할 것 
    
    private void work() {
        QueryRequest request;
        SQLiteDatabase db = getWritableDatabase();
        long newid = 0;

        while (isWorking) {
            if(listRequest.size() == 0) {
                isWorking = false;
            } else {
                request = listRequest.poll();
                ContentValues values = request.getData().refineToContentValues();

                newid = db.insert(NAME_TABLE, null, values);
                if(newid != 0) {
                    Log.e("Data Manager", "I save data in " + newid);
                } else {
                    Log.e("Data Manager", "I failed to save data");
                }
            }
        }
    }

    public void queryInsert(Datastone data) {
        listRequest.add(new QueryRequest(data, QueryRequest.QUERY_TYPE_INSERT));
        wake();
    }

    @Deprecated
    public void registerCallback(Queryable callback) {
        if(listCallback.size() <= 0) {
            listCallback.add(callback);
            Log.e("Data Manager", "Registering Complete");
        } else {
            boolean isNotExist = true;
            for(int i = 0; i < listCallback.size(); i++) {
                if(listCallback.get(i) == callback) {
                    isNotExist = false;
                }
            }
            if(isNotExist) {
                listCallback.add(callback);
                Log.e("Data Manager", "Registering Complete");
            }
        }
    }

    //---------------------------------------------------------------------------------------------//
    /* Todo: 데이터 매니저 새로 설계, 아래 메서드들은 추후 정리되거나 삭제되어야 함.
     */
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

    //Todo: SQL 쿼리 요청하는 메서드들은 모두 query 구문을 붇여 구분
    //Todo: 쿼리는 Queue에 저장하고 순차적으로 실행
    //Todo: 쿼리는 독립 스레드로 처리
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

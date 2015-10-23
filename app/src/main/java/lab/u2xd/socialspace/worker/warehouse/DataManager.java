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
import java.util.Queue;

import lab.u2xd.socialspace.worker.miner.ContactMiner;
import lab.u2xd.socialspace.worker.object.RefinedData;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;
import lab.u2xd.socialspace.worker.warehouse.objects.QueryRequest;
import lab.u2xd.socialspace.worker.warehouse.objects.Queryable;

/**
 * Created by ysb on 2015-09-30.
 */
public class DataManager extends SQLiteOpenHelper implements BaseColumns {

    private static DataManager object;

    public static final String NAME_DATABASE = "ContextDatabase";
    public static final String NAME_MAINTABLE = "ContextData";
    public static final String NAME_CONTACTTABLE = "ContactsData";
    public static final int VERSION_DATABASE = 12;

    public static final String FIELD_TYPE = "Type";
    public static final String FIELD_AGENT = "Agent";
    public static final String FIELD_TARGET = "Target";
    public static final String FIELD_TIME = "Time";
    public static final String FIELD_CONTENT = "Content";

    public static final String FIELD_NUMBER = "Number";
    public static final String FIELD_NAME = "Name";

    public static final String CONTEXT_TYPE_KAKAOTALK = "KakaoTalk";
    public static final String CONTEXT_TYPE_FACEBOOK= "Facebook";
    public static final String CONTEXT_TYPE_CALL = "Call";
    public static final String CONTEXT_TYPE_SMS = "SMS";
    public static final String CONTEXT_TYPE_MMS = "MMS";

    private static final String SQL_CREATE_MAINTABLE = "CREATE TABLE " + NAME_MAINTABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_TYPE + " TEXT, " +
            FIELD_AGENT + " TEXT, " +
            FIELD_TARGET + " TEXT, " +
            FIELD_TIME + " INTEGER, " +
            FIELD_CONTENT + " TEXT)";
    private static final String SQL_DROP_MAINTABLE = "DROP TABLE IF EXISTS " + NAME_MAINTABLE;
    private static final String SQL_GET_ALLMAIN = "SELECT * FROM " + NAME_MAINTABLE;
    private static final String SQL_CREATE_CONTACTTABLE ="CREATE TABLE " + NAME_CONTACTTABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_NUMBER + " TEXT, " +
            FIELD_NAME + " TEXT)";
    private static final String SQL_DROP_CONTACTTABLE = "DROP TABLE IF EXISTS " + NAME_CONTACTTABLE;
    private static final String SQL_GET_ALLCONTACT = "SELECT * FROM " + NAME_CONTACTTABLE;

    private static final String FILENAME_CSV = "CurrentDatabase.csv";



    //---------------------------------------------------------------------------------------------//
    private Queue<QueryRequest> listRequest;
    private boolean isWorking = false;

    private ArrayList<Queryable> listCallback;
    //---------------------------------------------------------------------------------------------//
    private Context context;
    private ContactMiner contactMiner;

    private DataManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        listRequest = new LinkedList<>();
        listCallback = new ArrayList<>();

        contactMiner = new ContactMiner();
        this.context = context;
        getWritableDatabase();
    }

    private void initializeContact(SQLiteDatabase db, Context context) {
        db.execSQL(SQL_DROP_CONTACTTABLE);
        db.execSQL(SQL_CREATE_CONTACTTABLE);
        Datastone[] stones = contactMiner.mineAllData(context);

        for(int i = 0; i < stones.length; i++) {
            queryInsert(NAME_CONTACTTABLE, stones[i]);
        }
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
        db.execSQL(SQL_CREATE_MAINTABLE);
        insertData(db, "General", "Me", "Me", 0, "Success!!");
        initializeContact(db, context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "I am making database, again!");
        db.execSQL(SQL_DROP_MAINTABLE);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "I am making old database, again!");
        super.onDowngrade(db, oldVersion, newVersion);
        db.execSQL(SQL_DROP_MAINTABLE);
        onCreate(db);
    }

    //---------------------------------------------------------------------------------------------//
    private void wake() {
        if(isWorking) {
            Log.e("Data Manager","I am already awaken!");
        } else {
            Log.e("Data Manager","Whoa! Ok, Ok, I am working.");
            isWorking = true;
            Thread tWork = new Thread(new Runnable() {
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
            tWork.start();
        }
    }

    // TODO: 2015-10-05 전화 및 문자 메시지 이벤트 데이터 저장 기능 완성할 것
    
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

                newid = db.insert(request.getTableName(), null, values);
                if(newid != 0) {
                    Log.e("Data Manager", "I save data in " + newid + " at " + request.getTableName());
                } else {
                    Log.e("Data Manager", "I failed to save data");
                }
            }
        }
        Log.e("Data Manager", "Query Processing Complete");
    }

    public void queryInsert(Datastone data) {
        listRequest.add(new QueryRequest(data, QueryRequest.QUERY_TYPE_INSERT));
        wake();
    }

    public void queryInsert(String table, Datastone data) {
        listRequest.add(new QueryRequest(table, data, QueryRequest.QUERY_TYPE_INSERT));
        wake();
    }

    public String getNameOfNumber(String AgentPhoneNumber) {
        SQLiteDatabase db = getWritableDatabase();
        //Cursor cursor = db.query(NAME_CONTACTTABLE, new String[]{ FIELD_NAME }, FIELD_NUMBER + "=" + AgentPhoneNumber, null, null, null, null);
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALLCONTACT, null);
        while(cursor.moveToNext()) {
            Log.e("Data Manager", cursor.getString(1) + ", " + cursor.getString(2));
            if(cursor.getString(1).equals(AgentPhoneNumber)) {
                return cursor.getString(2);
            }
        }
        return "Unknown";
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
    public void setRefinedData(RefinedData data) {
        insertData(getWritableDatabase(), data);
    }

    public String showAllData() {
        String str = "";
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALLMAIN, null);

        while(cursor.moveToNext()) {
            str += cursor.getInt(0) + " : " + cursor.getString(1) + ", Agent = " + cursor.getString(2) +
                    ", Time = " + cursor.getInt(4) +
                    ", Content = " + cursor.getString(5) + "\r\n \r\n";
        }
        return str;
    }


    public void reset() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DROP_MAINTABLE);
        db.execSQL(SQL_CREATE_MAINTABLE);
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
        newid = db.insert(NAME_MAINTABLE, null, values);
        if(newid != 0) {
            Log.e("Data Manager", "I save data in " + newid);
        } else {
            Log.e("Data Manager", "I failed to save data");
        }
    }

    public void exportDatabase() {
        String str = "ID,Type,Agent,Target,Time,Content\r\n";
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALLMAIN, null);
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

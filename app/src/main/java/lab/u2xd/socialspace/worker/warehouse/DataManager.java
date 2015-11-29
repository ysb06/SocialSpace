package lab.u2xd.socialspace.worker.warehouse;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
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
    public static final String NAME_EXPERIMENTTABLE = "ExperimentInfo";
    public static final int VERSION_DATABASE = 15;

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

    private static final String SQL_CREATE_EXPERIMENT_INFO = "CREATE TABLE " + NAME_EXPERIMENTTABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Name" + " TEXT, " +
            "Age" + " INTEGER, " +
            "Major" + " TEXT, " +
            "Gender" + " TEXT, " +
            "Phone" + " TEXT, " +
            "Email" + " TEXT," +
            "Agreement" + " INTEGER)";

    private static final String SQL_DROP_EXPERIMENT_INFO = "DROP TABLE IF EXISTS " + NAME_EXPERIMENTTABLE;
    private static final String SQL_GET_EXPERIMENT_INFO = "SELECT * FROM " + NAME_EXPERIMENTTABLE;

    private static final String FILENNAME_BASE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Context/";
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
        db.execSQL(SQL_CREATE_EXPERIMENT_INFO);
        insertData(db, "General", "Me", "Me", 0, "Success!!");
        initializeContact(db, context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "I am making database, again!");
        db.execSQL(SQL_DROP_MAINTABLE);
        db.execSQL(SQL_DROP_EXPERIMENT_INFO);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "I am making old database, again!");
        super.onDowngrade(db, oldVersion, newVersion);
        db.execSQL(SQL_DROP_MAINTABLE);
        db.execSQL(SQL_DROP_EXPERIMENT_INFO);
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

    public void queryInsert(Intent intentBasicInfoResult, int agreementType) {
        Datastone stone = new Datastone();

        stone.put("Name", intentBasicInfoResult.getStringExtra("expName"));
        stone.put("Age", intentBasicInfoResult.getIntExtra("expAge", 0));
        stone.put("Major", intentBasicInfoResult.getStringExtra("expMajor"));
        stone.put("Gender", intentBasicInfoResult.getStringExtra("expGender"));
        stone.put("Phone", intentBasicInfoResult.getStringExtra("expNumber"));
        stone.put("Email", intentBasicInfoResult.getStringExtra("expEmail"));
        stone.put("Agreement", agreementType);
        queryInsert(NAME_EXPERIMENTTABLE, stone);
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
        getWritableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALLCONTACT, null);
        while(cursor.moveToNext()) {
            if(cursor.getString(1).equals(AgentPhoneNumber)) {
                String temp = cursor.getString(2);
                cursor.close();
                return temp;
            }
        }
        cursor.close();
        return "Unknown";
    }

    public boolean isExperimentInfoRecorded() {
        getWritableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_EXPERIMENT_INFO, null);
        Log.e("Data Manager", "Experiment Size -> " + cursor.getCount());
        if(cursor.getCount() <= 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
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
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(FILENNAME_BASE);
            File sd = new File(FILENNAME_BASE + "ExportedDB");
            if(dir.mkdir()) {
                Log.e("Data Manager", "Context Folder Created");
            }
            if(sd.mkdir()) {
                Log.e("Data Manager", "DB Folder Created");
            }
            File data = Environment.getDataDirectory();
            FileChannel source = null;
            FileChannel destination = null;
            String currentDBPath = "/data/" + "lab.u2xd.socialspace" +"/databases/" + NAME_DATABASE;
            String backupDBPath = NAME_DATABASE + ".sqlite3";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
                Log.e("Data Manager", "Exporting DB Complete");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportDatabaseCSV() {
        String str = "ID,Type,Agent,Target,Time,Content\r\n";
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALLMAIN, null);
        while(cursor.moveToNext()) {
            str += cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3) + ","
                    //+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(cursor.getLong(4))) + "," + cursor.getString(5) + "\r\n";
                    + cursor.getLong(4) + "," + cursor.getString(5) + "\r\n";
        }
        writeCSV(str);
    }

    private boolean write(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(FILENNAME_BASE);
            if(dir.mkdir()) {
                Log.e("Data Manager", "I made an directory");
            }

            File file = new File(FILENNAME_BASE, FILENAME_CSV);
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

    private boolean writeCSV(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(FILENNAME_BASE);
            if(dir.mkdir()) {
                Log.e("Data Manager", "I made an directory");
            }

            File file = new File(FILENNAME_BASE, FILENAME_CSV);
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

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
import lab.u2xd.socialspace.worker.warehouse.objects.ProfileRaw;
import lab.u2xd.socialspace.worker.warehouse.objects.QueryRequest;
import lab.u2xd.socialspace.worker.warehouse.objects.Queryable;

/**
 * Created by ysb on 2015-09-30.
 */
public class DataManager extends SQLiteOpenHelper implements BaseColumns {

    private static DataManager object;

    public static final int VERSION_DATABASE = 17;
    //실험 시작단계의 데이터 베이스는 17임

    public static final String NAME_DATABASE = "ContextDatabase";
    public static final String NAME_MAINTABLE = "ContextData";
    public static final String NAME_CONTACTTABLE = "ContactsData";
    public static final String NAME_EXPERIMENTTABLE = "ExperimentInfo";
    public static final String NAME_SETTINGS = "Settings";
    public static final String NAME_QUESTIONAIRE = "Questionaire";

    public static final String FIELD_TYPE = "Type";
    public static final String FIELD_AGENT = "Agent";
    public static final String FIELD_TARGET = "Target";
    public static final String FIELD_TIME = "Time";
    public static final String FIELD_CONTENT = "Content";

    public static final String FIELD_NUMBER = "Number";
    public static final String FIELD_NAME = "Name";
    public static final String FIELD_PROFILE_IMAGE = "Profile";

    public static final String CONTEXT_TYPE_KAKAOTALK = "KakaoTalk";
    public static final String CONTEXT_TYPE_FACEBOOK= "Facebook";
    public static final String CONTEXT_TYPE_CALL = "Call";
    public static final String CONTEXT_TYPE_SMS = "SMS";
    public static final String CONTEXT_TYPE_MMS = "MMS";
    public static final String CONTEXT_TYPE_TWITTER = "Twitter";
    public static final String CONTEXT_TYPE_LINE = "Line";
    public static final String CONTEXT_TYPE_KAKAOSTORY = "KakaoStory";
    public static final String CONTEXT_TYPE_BAND = "Band";
    public static final String[] CONTEXT_TYPE_ALL = {CONTEXT_TYPE_CALL, CONTEXT_TYPE_SMS, CONTEXT_TYPE_KAKAOTALK, CONTEXT_TYPE_KAKAOSTORY, CONTEXT_TYPE_FACEBOOK, CONTEXT_TYPE_TWITTER, CONTEXT_TYPE_LINE, CONTEXT_TYPE_BAND};

    //메인 Context 데이터베이스 관련
    private static final String SQL_CREATE_MAINTABLE = "CREATE TABLE " + NAME_MAINTABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_TYPE + " TEXT, " +
            FIELD_AGENT + " TEXT, " +
            FIELD_TARGET + " TEXT, " +
            FIELD_TIME + " INTEGER, " +
            FIELD_CONTENT + " TEXT)";
    private static final String SQL_DROP_MAINTABLE = "DROP TABLE IF EXISTS " + NAME_MAINTABLE;
    private static final String SQL_GET_ALLMAIN = "SELECT * FROM " + NAME_MAINTABLE;
    //연락처 데이터베이스 관련
    private static final String SQL_CREATE_CONTACTTABLE ="CREATE TABLE " + NAME_CONTACTTABLE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_NUMBER + " TEXT, " +
            FIELD_NAME + " TEXT, " +
            FIELD_PROFILE_IMAGE + " TEXT)";     //프로필 이미지는 경로로 저장
    private static final String SQL_SET_IMAGE_PATH = "";
    private static final String SQL_DROP_CONTACTTABLE = "DROP TABLE IF EXISTS " + NAME_CONTACTTABLE;
    private static final String SQL_GET_ALLCONTACT = "SELECT * FROM " + NAME_CONTACTTABLE;
    //실험 데이터베이스 관련
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

    //환경설정 데이터베이스 관련
    private static final String SETTING_INITIALIZED = "Initialized";
    private static final String SQL_CREATE_SETTINGS = "CREATE TABLE " + NAME_SETTINGS + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_TYPE + " TEXT, " +
            FIELD_CONTENT + " TEXT)";
    private static final String SQL_DROP_SETTINGS = "DROP TABLE IF EXISTS " + NAME_SETTINGS;
    private static final String SQL_GET_SETTING_INITIALIZED = "SELECT * FROM " + NAME_SETTINGS + " WHERE " + FIELD_TYPE + " = '" + SETTING_INITIALIZED + "';";
    private static final String SQL_DELETE_SETTING_INITIALIZED = "DELETE FROM " + NAME_SETTINGS + " WHERE " + FIELD_TYPE + " = '" + SETTING_INITIALIZED + "';";
    //설문조사 관련, 현재 사용하지 않음
    private static final String SQL_CREATE_QUESTIONAIRE = "CREATE TABLE " + NAME_QUESTIONAIRE + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FIELD_NAME + " TEXT, " +
            FIELD_CONTENT + " INTEGER)";
    private static final String SQL_DROP_QUESTIONAIRE = "DROP TABLE IF EXISTS " + NAME_QUESTIONAIRE;


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
        Log.i("Data Manager", "Database is initialized -> " + db.isOpen());
    }

    public static DataManager getManager(Context context) {
        if(object == null) {
            object = new DataManager(context.getApplicationContext(), NAME_DATABASE, null, VERSION_DATABASE);
        }
        return object;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MAINTABLE);
        db.execSQL(SQL_CREATE_EXPERIMENT_INFO);
        db.execSQL(SQL_CREATE_SETTINGS);
        db.execSQL(SQL_CREATE_QUESTIONAIRE);
        insertData(db, "General", "Me", "Me", 0, "Success!!");
        initializeContact(db, context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "Database is Upgraded");
        db.execSQL(SQL_DROP_MAINTABLE);
        db.execSQL(SQL_DROP_EXPERIMENT_INFO);
        db.execSQL(SQL_DROP_SETTINGS);
        db.execSQL(SQL_DROP_QUESTIONAIRE);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("Data Manager", "Database is Downgraded");
        super.onDowngrade(db, oldVersion, newVersion);
        db.execSQL(SQL_DROP_MAINTABLE);
        db.execSQL(SQL_DROP_EXPERIMENT_INFO);
        db.execSQL(SQL_DROP_SETTINGS);
        db.execSQL(SQL_DROP_QUESTIONAIRE);
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

    /* 가능하다면 SELECT 문도 여기서 처리할 수 있도록 하면 좋을 듯. 지금은 귀찮아서 하지 않겠음
    * 솔직히 이 데이터 매니저 구조 자체가 아주 비효율적으로 보임
    * Datastone 객체로 변환했다 다시 ContentValue로 변환하는 과정에서 상당한 시간적 손실이 있는 것으로 보임
    * 이러한 Time Delay때문에 어쩔 수 없이 SQL 요청 시 따로 스레드를 만들어 처리하는 구조가 되어 있음
    * 더 가능하다면 이 데이터 처리 구조 자체를 바꾸면 좋겠지만 그럴 경우 앱을 갈아 엎는 형식이 될 거임. */
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
                //만약 쿼리 타입 중에 ContentValues를 쓰지 않는 경우가 있다면 위 코드 1줄 부분은 if문 안쪽으로 각각 넣어 줄 것

                if(request.getType() == QueryRequest.QUERY_TYPE_INSERT) {                       // Insert 쿼리
                    newid = db.insert(request.getTableName(), null, values);
                    if (newid != 0) {
                        Log.i("Data Manager", "I save data in " + newid + " at " + request.getTableName());
                    } else {
                        Log.e("Data Manager", "I failed to save data");
                    }
                } else if(request.getType() == QueryRequest.QUERY_TYPE_UPDATE) {
                    String where = request.getWhere() + "=?";
                    newid = db.update(request.getTableName(), values, where, request.getWhereConditions());
                    if (newid != 0) {
                        Log.i("Data Manager", "Update Complete in " + newid + " at " + request.getTableName());
                    } else {
                        Log.e("Data Manager", "I failed to update data");
                    }
                } else {
                    Log.wtf("Data Manager", "Unknown Query Type! You must define Query Type.");
                }
            }
        }
        Log.i("Data Manager", "Query Processing Complete");
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

    /** 메인 Context 테이블에 데이터 저장 쿼리
     *
     * @param data
     */
    public void queryInsert(Datastone data) {
        listRequest.add(new QueryRequest(data, QueryRequest.QUERY_TYPE_INSERT));
        wake();
    }

    public void queryInsert(String table, Datastone data) {
        listRequest.add(new QueryRequest(table, data, QueryRequest.QUERY_TYPE_INSERT));
        wake();
    }

    public String queryNameOfNumber(String AgentPhoneNumber) {
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


    public void queryImageNameUpdate(String name, String path) {
        Datastone data = new Datastone();
        data.put(FIELD_PROFILE_IMAGE, path);

        listRequest.add(new QueryRequest(NAME_CONTACTTABLE, data, FIELD_NAME, new String[] { name }, QueryRequest.QUERY_TYPE_UPDATE));
        wake();
    }

    //실험 정보 관련, 추후 서비스 개인정보 테이블로도 쓸 수 있음
    public boolean isExperimentInfoRecorded() {
        getWritableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_EXPERIMENT_INFO, null);
        Log.i("Data Manager", "Experiment Size -> " + cursor.getCount());
        if(cursor.getCount() <= 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    // Setting 변경 관련
    public boolean querySettingInitialized() {
        getWritableDatabase();
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_SETTING_INITIALIZED, null);
        if(cursor.getCount() <= 0) {
            Datastone stone = new Datastone();
            stone.put(FIELD_TYPE, SETTING_INITIALIZED);
            stone.put(FIELD_CONTENT, "True");

            queryInsert(NAME_SETTINGS, stone);
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public void querySettingInitializedRemove() {
        getWritableDatabase().execSQL(SQL_DELETE_SETTING_INITIALIZED);
    }

    //Context 테이블 읽기 쿼리
    public ArrayList<ProfileRaw> queryContextData() {
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALLMAIN + " order by " + FIELD_TIME + " desc", null);
        ArrayList<ProfileRaw> listName = new ArrayList<>();
        Log.i("Data Manager", "Query Complete -> " + cursor.getCount());

        if(cursor.moveToFirst()) {
            do {
                if(!cursor.isNull(2)) {
                    ProfileRaw target = new ProfileRaw(cursor.getString(2));

                    boolean isNameNotOnList = true;
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).name.equals(cursor.getString(2))) {
                            target = listName.get(i);
                            isNameNotOnList = false;
                        }
                    }
                    if (isNameNotOnList) {  //여기가 문제
                        listName.add(target);
                    }

                    for (int i = 0; i < CONTEXT_TYPE_ALL.length; i++) {
                        if (CONTEXT_TYPE_ALL[i].equals(cursor.getString(1))) {
                            target.count[i]++;

                            target.listEvent.add(i);
                            target.listTime.add(cursor.getLong(4));
                        }
                    }
                }
            } while (cursor.moveToNext());

            Log.i("Data Manager", "Result -> " + listName.size());
        }
        return listName;
    }

    // Contact
    public String queryBitmapPathOfName(String name) {
        String path = "";
        Cursor cursor = getReadableDatabase().rawQuery(SQL_GET_ALLCONTACT, null);
        while(cursor.moveToNext()) {
            if(cursor.getString(2).equals(name)) {
                path = cursor.getString(3);
                cursor.close();
                return path;
            }
        }
        cursor.close();
        return null;
    }

    //------------------------ 이 밑으로는 안 쓰이거나 쓰지 말아야 할 코드 ------------------------//

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
        cursor.close();
        return str;
    }

    /** 데이터베이스 초기화 메서드, 데이터 구조가 변경되는 것 외에는 데이터베이스는 기본적으로 지워지면 안됨. 데이터 구조 변경 시 데이터베이스 버전을 바꾸는 것으로 리셋 대체 */
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

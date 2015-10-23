package lab.u2xd.socialspace.worker.miner;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/** 데이터 획득 서비스, 항시 백그라운드에 상주하며
 * Created by ysb on 2015-09-25.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NotificationMiner extends NotificationListenerService {

    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";

    public static final String LOG_FILENAME = "MiningReport00.txt";

    private DataManager dbManager;

    @Override
    public void onCreate() {
        Log.e("Notification Miner", "We are starting to work");
        dbManager = DataManager.getManager(this);

        /*
        Log.e("Miner Manager", "Querying Call Log");
        RefinedData[] data1 = callMiner.mineAllData(this);
        for(int i = 0; i < data1.length; i++) {
            dbManager.setRefinedData(data1[i]);
        }
        Log.e("Miner Manager", "Querying SMS");
        RefinedData[] data2 = smsMiner.mineAllData(this);
        for(int i = 0; i < data2.length; i++) {
            dbManager.setRefinedData(data2[i]);
        }
        */
    }

    @Override
    public void onDestroy() {
        Log.e("Notification Miner", "We are finishing working");
        dbManager.close();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Datastone data = mineStatusBarNotification(sbn);
        if(data != null)
            dbManager.queryInsert(data);
        writeLog(getNotificationLog(sbn));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("Notification Miner", "I feel something disappeared!");
    }

    /** 상태 바 알림을 Datastone으로 가공
     *
     * @param sbn 상태 바 알림
     * @return 가공된 데이터
     */
    private Datastone mineStatusBarNotification(StatusBarNotification sbn) {
        Notification noti = sbn.getNotification();
        Datastone datastone = new Datastone();

        Log.e("Notification Miner", "I found something -> " + sbn.getPackageName());

        if(sbn.getPackageName().equals("com.kakao.talk")) {                     //카카오톡
            Log.e("Notification Miner", "It is KakaoTalk data!");

            datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_KAKAOTALK);
            datastone.put(DataManager.FIELD_AGENT, noti.extras.getString(EXTRA_TITLE));
            datastone.put(DataManager.FIELD_TARGET, "Me");
            datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
            datastone.put(DataManager.FIELD_CONTENT, noti.extras.getString(EXTRA_TEXT));
        } else if(sbn.getPackageName().equals("com.facebook.katana")) {         //페이스북
            Log.e("Notification Miner", "Facebook data!");

            String str = noti.extras.getString(EXTRA_TEXT);
            String sAgent = "";
            int iIndexSTR = str.indexOf("님이");
            if(iIndexSTR != -1) {
                sAgent = str.substring(0, iIndexSTR);
            } else {
                sAgent = "Unknown";
            }
            datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_FACEBOOK);
            datastone.put(DataManager.FIELD_AGENT, sAgent);
            datastone.put(DataManager.FIELD_TARGET, "Me");
            datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
            datastone.put(DataManager.FIELD_CONTENT, "타임라인");
        } else if(sbn.getPackageName().equals("com.facebook.orca")) {           //페이스북 메신저
            String sTitle = noti.extras.getString(EXTRA_TITLE);
            if(sTitle.equals("null")) {
                Log.e("Notification Miner", "It looks Facebook message data, but it is not, I think.");
                return null;
            } else {
                Log.e("Notification Miner", "Facebook message data!");

                datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_FACEBOOK);
                datastone.put(DataManager.FIELD_AGENT, sTitle);
                datastone.put(DataManager.FIELD_TARGET, "Me");
                datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
                datastone.put(DataManager.FIELD_CONTENT, noti.extras.getString(EXTRA_TEXT));
            }
        } else {
            Log.e("Notification Miner", "It is nothing. Sorry");
            return null;
        }
        return datastone;
    }

    private String getNotificationLog(StatusBarNotification sbn) {
        String type = "";

        if(sbn.getPackageName().equals("com.kakao.talk")) {
            type = DataManager.CONTEXT_TYPE_KAKAOTALK;
        } else {
            type = sbn.getPackageName();
        }
        return "Noti : " + type + ", " + sbn.getNotification().extras.getString(EXTRA_TITLE) + ", " + sbn.getNotification().extras.getString(EXTRA_TEXT)
                + "\r\n" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }


    private boolean writeLog(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String sSDdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Context/";
            File dir = new File(sSDdir);
            if(dir.mkdir()) {
                Log.e("Miner Manager", "I made an directory");
            }

            File file = new File(sSDdir, LOG_FILENAME);
            try {
                if(!file.exists()) {
                    file.createNewFile();
                    Log.e("Miner Manager", "File is created");
                }
                String path = file.getAbsolutePath();
                Log.e("Miner Manager", "Reading... " + path);

                FileWriter writer = new FileWriter(file, true);
                writer.append(str + "\r\n");
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            Log.e("Miner Manager", "There is no storage");
            return false;
        }
    }
    // TODO: 2015-10-12 안드로이드 젤리빈 이전 버전에 대한 지원 추가
}

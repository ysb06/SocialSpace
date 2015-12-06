package lab.u2xd.socialspace.worker.miner;

import android.annotation.TargetApi;
import android.app.Notification;
import android.graphics.Bitmap;
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

import lab.u2xd.socialspace.worker.miner.object.NotificationPickaxe;
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

    public static final String LOG_FILENAME = "StatusMiningLog.txt";

    private DataManager dbManager;

    @Override
    public void onCreate() {
        Log.e("Notification Miner", "We are starting to work");
        dbManager = DataManager.getManager(this);
    }

    @Override
    public void onDestroy() {
        Log.e("Notification Miner", "We are finishing working");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        String[] str = new String[4];
        str[0] = notification.extras.getString(EXTRA_TITLE) + "|" + notification.extras.getString(EXTRA_TEXT);
        str[1] = notification.extras.getString(EXTRA_TITLE);
        str[2] = notification.extras.getString(EXTRA_TEXT);
        str[3] = notification.extras.getString(EXTRA_TEXT);

        Bitmap icon = notification.extras.getParcelable(EXTRA_LARGE_ICON);
        Datastone data = NotificationPickaxe.mine(this, sbn.getPackageName(), str, false, icon);

        if(data != null)
            dbManager.queryInsert(data);
        writeLog(getNotificationLog(sbn));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("Notification Miner", "I feel something disappeared!");
    }

    //------------------------------------------------------------------//
    //로깅 기능 추후 비활성화
    private String getNotificationLog(StatusBarNotification sbn) {
        String type = "";

        if(sbn.getPackageName().equals("com.kakao.talk")) {
            type = DataManager.CONTEXT_TYPE_KAKAOTALK;
        } else {
            type = sbn.getPackageName();
        }
        return "Noti : " + type + ", " + sbn.getNotification().extras.getString(EXTRA_TITLE) + ", " + sbn.getNotification().extras.getString(EXTRA_TEXT)
                + ", " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
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
}

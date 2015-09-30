package lab.u2xd.socialspace.miner;

import android.app.Notification;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ysb on 2015-09-25.
 */
public class NotificationMiner extends NotificationListenerService {

    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";

    public static final String LOG_FILENAME = "Notification_Log00.txt";

    private DataManager dbManager;

    @Override
    public void onCreate() {
        Log.e("Noti Miner", "I start to work");
        dbManager = new DataManager(this);
    }

    @Override
    public void onDestroy() {
        Log.e("Noti Miner", "I will not work");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.e("Noti Miner", "I got a message!");
        dbManager.setStatusBarNotification(sbn);

        Notification noti = sbn.getNotification();
        Date dateNow = new Date(System.currentTimeMillis());
        writeLog("AYBABTU : " + noti.extras.getString(EXTRA_TITLE) + ",\t\t" + noti.extras.getString(EXTRA_TEXT)
                + "\r\n" + new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초").format(dateNow) + ",\t\t" + Notification.EXTRA_MEDIA_SESSION + "\r\n");

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("Noti Miner", "I feel something disappeared!");
    }

    private boolean writeLog(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String sSDdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Context/";
            File dir = new File(sSDdir);
            if(dir.mkdir()) {
                Log.e("Noti Miner", "I made an directory");
            }

            File file = new File(sSDdir, LOG_FILENAME);
            try {
                if(!file.exists()) {
                    file.createNewFile();
                    Log.e("Noti Miner", "File is created");
                }
                String path = file.getAbsolutePath();
                Log.e("Noti Miner", "Reading... " + path);

                FileWriter writer = new FileWriter(file, true);
                writer.append(str + "\r\n");
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            Log.e("Noti Miner", "There is no storage");
            return false;
        }
    }
}

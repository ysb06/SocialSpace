package lab.u2xd.socialspace;

import android.app.Notification;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Noti Miner", "I start to work");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Noti Miner", "I will not work");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.e("Noti Miner", "I got a message!");
        Notification noti = sbn.getNotification();
        Date dateNow = new Date(System.currentTimeMillis());
        saveStringToStorage("AYBABTU : " + noti.extras.getString(EXTRA_TITLE) + ",\t\t" + noti.extras.getString(EXTRA_TEXT)
                + "\r\n" + new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초").format(dateNow) + "\r\n");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("Noti Miner", "I feel something disappeared!");
    }

    private boolean saveStringToStorage(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String sSDdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Context/";
            File dir = new File(sSDdir);
            if(dir.mkdir()) {
                Log.e("Noti Miner", "I made an directory");
            }

            File file = new File(sSDdir, "Notification_Log.txt");
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
            Log.e("Noti Miner Service", "There is no storage");
            return false;
        }
    }
}

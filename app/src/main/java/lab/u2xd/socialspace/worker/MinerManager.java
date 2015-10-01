package lab.u2xd.socialspace.worker;

import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import lab.u2xd.socialspace.worker.miner.CallMiner;
import lab.u2xd.socialspace.worker.miner.NotificationMiner;
import lab.u2xd.socialspace.worker.miner.PhoneLogMiner;
import lab.u2xd.socialspace.worker.miner.SMSMiner;
import lab.u2xd.socialspace.worker.object.RefinedData;

/** 데이터 획득 서비스, 항시 백그라운드에 상주하며
 * Created by ysb on 2015-09-25.
 */
public class MinerManager extends NotificationListenerService {

    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";

    public static final String LOG_FILENAME = "MiningReport00.txt";

    private DataManager dbManager;
    private NotificationMiner notificationMiner;
    private CallMiner callMiner;
    private SMSMiner smsMiner;

    @Override
    public void onCreate() {
        Log.e("Miner Manager", "We are starting to work");
        dbManager = new DataManager(this);
        notificationMiner = new NotificationMiner();
        callMiner = new CallMiner();
        smsMiner = new SMSMiner(this);

        Log.e("Miner Manager", "Querying Call Log");
        RefinedData[] data1 = callMiner.queryAllPastData(this);
        for(int i = 0; i < data1.length; i++) {
            dbManager.setRefinedData(data1[i]);
        }
        Log.e("Miner Manager", "Querying SMS");
        RefinedData[] data2 = smsMiner.queryAllPastData(this);
        for(int i = 0; i < data2.length; i++) {
            dbManager.setRefinedData(data2[i]);
        }
    }

    @Override
    public void onDestroy() {
        Log.e("Miner Manager", "We are finishing working");
        dbManager.close();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        dbManager.setRefinedData(notificationMiner.smeltStatusBarNotification(sbn));
        writeLog(notificationMiner.getNotificationLog(sbn));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("Miner Manager", "I feel something disappeared!");
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

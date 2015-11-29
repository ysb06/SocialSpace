package lab.u2xd.socialspace.worker.miner;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lab.u2xd.socialspace.worker.miner.objects.NotificationPickaxe;
import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/** API 19 미만에서 사용되는 Notification Miner
 * Created by ysb on 2015-11-24. */
public class NotificationMinerCompat extends AccessibilityService {

    public static final String LOG_FILENAME = "StatusMiningCompatLog.txt";

    private DataManager dbManager;

    @Override
    public void onCreate() {
        Log.e("Notification Miner C", "I Created!");
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.notificationTimeout = 100;
        info.packageNames = null;
        info.feedbackType = AccessibilityServiceInfo.DEFAULT;
        this.setServiceInfo(info);

        dbManager = DataManager.getManager(this);
        Log.e("Notification Miner C", "Service Info set");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Log.e("Notification Miner C", event.getPackageName().toString());
            String[] str = getText(event);
            str[0] = event.getText().toString();
            Datastone data = NotificationPickaxe.mine(event.getPackageName().toString(), str, true);
            if(data != null)
                dbManager.queryInsert(data);
            writeLog(getNotificationLog(event.getPackageName().toString(), str));
        }
    }

    private String[] getText(AccessibilityEvent event) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Notification notification = (Notification) event.getParcelableData();
        String[] str = new String[4];
        for (int i = 0; i < str.length; i++) {
            str[i] = "Error";
        }
        if(notification == null || inflater == null) {
            return str;
        } else {
            RemoteViews remoteView = notification.bigContentView;
            if(remoteView == null) {
                return str;
            }
            ViewGroup localView = (ViewGroup) inflater.inflate(remoteView.getLayoutId(), null);
            remoteView.reapply(getApplicationContext(), localView);
            Resources resources = null;
            PackageManager pkm = getPackageManager();
            try {
                resources = pkm.getResourcesForApplication("lab.u2xd.socialspace");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (resources == null) {
                return str;
            }

            int TITLE = resources.getIdentifier("android:id/title", null, null);
            int INBOX = resources.getIdentifier("android:id/big_text", null, null);
            int TEXT = resources.getIdentifier("android:id/text", null, null);

            TextView title = (TextView) localView.findViewById(TITLE);
            TextView inbox = (TextView) localView.findViewById(INBOX);
            TextView text = (TextView) localView.findViewById(TEXT);

            if(title != null) {
                Log.e("Notification Miner C", "Title -> " + title.getText().toString());
                str[1] = title.getText().toString();
            } else {
                str[1] = "Null";
            }
            if(inbox != null) {
                Log.e("Notification Miner C", "Content1 -> " + inbox.getText().toString());
                str[2] = inbox.getText().toString();
            } else {
                str[2] = "Null";
            }
            if(text != null) {
                Log.e("Notification Miner C", "Content2 -> " + text.getText().toString());
                str[3] = text.getText().toString();
            } else {
                str[3] = "Null";
            }

        }
        return str;
    }

    @Override
    public void onInterrupt() {

    }

    private String getNotificationLog(String packageName, String[] notification) {
        String type = "";

        if(packageName.equals("com.kakao.talk")) {
            type = DataManager.CONTEXT_TYPE_KAKAOTALK;
        } else {
            type = packageName;
        }
        return "Noti : " + type + ", " + notification[0] + ", " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }


    private boolean writeLog(String str) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String sSDdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Context/";
            File dir = new File(sSDdir);
            if(dir.mkdir()) {
                Log.e("Notification Miner C", "Logging Start");
            }

            File file = new File(sSDdir, LOG_FILENAME);
            try {
                if(!file.exists()) {
                    file.createNewFile();
                    Log.e("Notification Miner C", "File is created");
                }
                String path = file.getAbsolutePath();
                Log.e("Notification Miner C", "Reading... " + path);

                FileWriter writer = new FileWriter(file, true);
                writer.append(str + "\r\n");
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            Log.e("Notification Miner C", "There is no storage");
            return false;
        }
    }
}

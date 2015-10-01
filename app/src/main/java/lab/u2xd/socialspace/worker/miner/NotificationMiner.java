package lab.u2xd.socialspace.worker.miner;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import lab.u2xd.socialspace.worker.DataManager;
import lab.u2xd.socialspace.worker.object.RefinedData;

/**
 * Created by yim on 2015-10-01.
 */
public class NotificationMiner {

    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";



    public RefinedData smeltStatusBarNotification(StatusBarNotification sbn) {
        Log.e("Notification Miner", "I got something");
        Notification noti = sbn.getNotification();

        if(sbn.getPackageName().equals("com.kakao.talk")) {
            Log.e("Notification Miner", "This is KakaoTalk!");

            return new RefinedData(DataManager.TYPE_KAKAOTALK, noti.extras.getString(EXTRA_TITLE), noti.extras.getString(EXTRA_TEXT));
        } else {
            Log.e("Notification Miner", "I think it is waste");
            return null;
        }
    }

    public String getNotificationLog(StatusBarNotification sbn) {
        String type = "";

        if(sbn.getPackageName().equals("com.kakao.talk")) {
            type = DataManager.TYPE_KAKAOTALK;
        } else {
            type = sbn.getPackageName();
        }
        return "Noti : " + type + ", " + sbn.getNotification().extras.getString(EXTRA_TITLE) + ", " + sbn.getNotification().extras.getString(EXTRA_TEXT)
                + "\r\n" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }
}

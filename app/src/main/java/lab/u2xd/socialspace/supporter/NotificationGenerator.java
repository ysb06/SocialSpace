package lab.u2xd.socialspace.supporter;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import lab.u2xd.socialspace.MainActivity;
import lab.u2xd.socialspace.R;

/**
 * Created by ysb on 2015-09-25.
 */
public class NotificationGenerator {

    NotificationManager notificationManager;

    /** 알림 생성 및 삭제
     *
     * @param context 현재 객체를 생성하는 주체 Context */
    public NotificationGenerator(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /** 테스트 용 아무 알림 하나 생성함
     *
     * @param context 현재 알림을 요청하는 Context
     */
    public void generateNotification(Context context) {
        generateNotification(context, context.getString(R.string.app_name), "All your base belong to us");
    }

    /** 정해진 제목과 내용으로 알림을 생성함
     *
     * @param context 현재 알림을 요청하는 Context
     * @param title 알림 제목
     * @param message 알림 내용
     */
    public void generateNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);

        PendingIntent pintent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pintent);

        notificationManager.notify(7601, builder.build());
    }

    public void closeAllNotification() {
        notificationManager.cancelAll();
    }
}

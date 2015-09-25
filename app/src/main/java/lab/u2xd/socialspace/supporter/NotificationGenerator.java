package lab.u2xd.socialspace.supporter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import lab.u2xd.socialspace.MainActivity;
import lab.u2xd.socialspace.R;

/**
 * Created by ysb on 2015-09-25.
 */
public class NotificationGenerator {

    public void generateNotification(Context context) {
        generateNotification(context, context.getString(R.string.app_name), "All your base belong to us");
    }

    public void generateNotification(Context context, String title, String message) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(message);


        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        builder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(7601, builder.build());
    }
}

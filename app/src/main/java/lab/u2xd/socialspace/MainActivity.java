package lab.u2xd.socialspace;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import lab.u2xd.socialspace.miner.DataManager;
import lab.u2xd.socialspace.supporter.NotificationGenerator;

/** 첫 화면 UI 및 모든 이벤트 관리
 *
 */
public class MainActivity extends AppCompatActivity {

    //Support Team
    NotificationGenerator notiMaker;
    DataManager dataManager;

    //UI
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notiMaker = new NotificationGenerator();
        dataManager = new DataManager(this);

        textView = (TextView) findViewById(R.id.textView);

        if (!isContainedInNotificationListeners(getApplicationContext())) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivityForResult(intent, 2222);
        }
    }

    private static boolean isContainedInNotificationListeners(Context context)
    {
        String enabledListeners = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        return !TextUtils.isEmpty(enabledListeners) && enabledListeners.contains(context.getPackageName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //이벤트
    public void Execute_Click(View view) {
        notiMaker.generateNotification(getApplicationContext());
        String str = dataManager.showAllData();
        textView.setText(str);
    }
}

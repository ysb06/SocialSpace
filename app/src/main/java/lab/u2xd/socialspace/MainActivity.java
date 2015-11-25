package lab.u2xd.socialspace;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import lab.u2xd.socialspace.worker.miner.CallEventMiner;
import lab.u2xd.socialspace.worker.miner.CallMiner;
import lab.u2xd.socialspace.worker.miner.SMSMiner;
import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.supporter.NotificationGenerator;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;
import lab.u2xd.socialspace.worker.warehouse.objects.Queryable;

/** 첫 화면 UI 및 모든 이벤트 관리
 *
 */
public class MainActivity extends AppCompatActivity implements Queryable{

    //Support Team
    NotificationGenerator notiMaker;
    DataManager dataManager;

    //UI
    ArrayList<View> listView;
    EditText textInputBox;
    TextView textView;
    ProgressBar bar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notiMaker.closeAllNotification();
        dataManager.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notiMaker = new NotificationGenerator(this);
        dataManager = DataManager.getManager(this);

        textInputBox = (EditText) findViewById(R.id.adminPass);
        textView = (TextView) findViewById(R.id.textView);
        listView = new ArrayList<>();
        listView.add(findViewById(R.id.main_button_execute));
        listView.add(findViewById(R.id.main_button_notigen));
        listView.add(findViewById(R.id.main_button_readlog));
        listView.add(findViewById(R.id.main_button_datadelete));
        listView.add(textView);
        
        if(Build.VERSION.SDK_INT >= 19) {
            if (!isContainedInNotificationListeners(getApplicationContext())) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivityForResult(intent, 2222);
            }
        } else if(Build.VERSION.SDK_INT >= 16) {

        } else {
            Log.e("Main Activity", "Current SDK is under 16. This app may not work properly.");
            Toast.makeText(MainActivity.this, "Warning : This Device is using old Android", Toast.LENGTH_SHORT).show();
        }
        bar = (ProgressBar) findViewById(R.id.main_progressBar);
        bar.setVisibility(View.INVISIBLE);

        if(!isCallEventMinerServiceStarted()) {
            startService(new Intent(this, CallEventMiner.class));
        }
    }

    /** 현재 Notification Miner가 서비스에 등록이 되어 있는지 확인하는 코드
     *
     * @param context 현재 액티비티
     * @return 서비스 등록 여부 */
    private boolean isContainedInNotificationListeners(Context context)
    {
        String enabledListeners = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        return !TextUtils.isEmpty(enabledListeners) && enabledListeners.contains(context.getPackageName());
    }

    private boolean isCallEventMinerServiceStarted() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(info.service.getClassName().equals("lab.u2xd.socialspace.worker.miner.CallEventMiner")) {
                Log.e("Main Activity","CallEvent Service is already running!");
                return true;
            }
        }
        return false;
    }

    //--------------onCreate 완료--------------//

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

    //Execute 버튼 클릭 이벤트
    public void Execute_Click(View view) {
        String str = dataManager.showAllData();
        dataManager.exportDatabase();
        textView.setText(str);
        sendBroadcast(new Intent("lab.u2xd.socialspace.miner.PHONELOG"));
    }

    //Notigen 버튼 클릭 이벤트
    public void NotiGen_Click(View view) {
        notiMaker.generateNotification(getApplicationContext());
        textView.setText(dataManager.getNameOfNumber("01000001234"));
    }

    /**데이터 삭제 버튼 클릭 이벤트
     *
     * @param view 사용자가 클릭한 Button View
     */
    public void DataDelete_Click(View view) {
        dataManager.reset();
    }

    /** 전화, 문자 읽기 기능 실행 버튼 클릭 이벤트
     *
     * @param view 사용자가 클릭한 Button View
     */
    public void ReadLog_Click(View view) {
        Datastone[] call = CallMiner.getMiner().mineAllData(this);
        Datastone[] sms = SMSMiner.getMiner(this).mineAllData(this);
        // TODO: 2015-11-23 추후에는 문자는 데이터 업데이트 형식으로 읽을 것
        for(int i = 0; i < call.length; i++) {
            dataManager.queryInsert(call[i]);
        }
        for(int i = 0; i < sms.length; i++) {
            dataManager.queryInsert(sms[i]);
        }
        bar.setVisibility(View.VISIBLE);
    }

    public void Input_Click(View view) {
        bar.setVisibility(View.INVISIBLE);
        if(textInputBox.getText().toString().equals("u2xd lab")) {
            for (int i = 0; i < listView.size(); i++) {
                listView.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFinish_Query() {
        bar.setVisibility(View.INVISIBLE);
    }
}

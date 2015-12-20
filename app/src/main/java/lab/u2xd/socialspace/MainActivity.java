package lab.u2xd.socialspace;

import android.accessibilityservice.AccessibilityServiceInfo;
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
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lab.u2xd.socialspace.experimenter.InfoAgreement;
import lab.u2xd.socialspace.experimenter.BasicInfo;
import lab.u2xd.socialspace.servicer.ui.SpaceField;
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

    static final int REQUEST_GET_BASIC_INFO = 1;
    static final int REQUEST_GET_PERSONAL_INFOMATION_AGREEMENT = 2;
    static final int REQUEST_SERVICE = 3;
    static final int REQUEST_START_NOTIFICATION_SERVICE = 222;
    static final int REQUEST_START_ACCESSIBILITY_SERVICE = 222;

    //Support Team
    private NotificationGenerator notiMaker;
    private DataManager dataManager;

    //UI
    private ArrayList<View> listView;
    private EditText textInputBox;
    private TextView textView;
    private ProgressBar bar;

    private int iAgreement = 0;

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
        Button btnComplete = (Button) findViewById(R.id.main_button_complete);
        listView.add(btnComplete);
        listView.add(textView);

        bar = (ProgressBar) findViewById(R.id.main_progressBar);

        if(dataManager.isExperimentInfoRecorded()) {
            Intent intentExp = new Intent(this, InfoAgreement.class);
            startActivityForResult(intentExp, REQUEST_GET_PERSONAL_INFOMATION_AGREEMENT);
        }

        if(Build.VERSION.SDK_INT >= 19) {
            if (!isContainedInNotificationListeners(getApplicationContext())) {
                Toast.makeText(this, "SocialSpace를 활성화시켜 주세요", Toast.LENGTH_LONG).show();

                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivityForResult(intent, REQUEST_START_NOTIFICATION_SERVICE);
            }
        } else if(Build.VERSION.SDK_INT < 19 && Build.VERSION.SDK_INT >= 16) {
            Log.e("Main Activity","Notification Miner run as compat mode");
            if(isNotificationMinerCompatStarted()) {
                Log.e("Main Activity","Notification Miner Compat Mode was already started.");
            } else {
                Toast.makeText(this, "접근성의 SocialSpace를 활성화시켜 주세요", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, REQUEST_START_ACCESSIBILITY_SERVICE);
            }
        } else {
            Log.e("Main Activity", "Current SDK is under 16. This app may not work properly.");
            Toast.makeText(MainActivity.this, "Warning : This Device is using old Android", Toast.LENGTH_SHORT).show();
        }

        if(!isCallEventMinerServiceStarted()) {
            startService(new Intent(this, CallEventMiner.class));
        }

        if(dataManager.querySettingInitialized()) {
            Log.e("Main Activity", "Relaunched");
        } else {
            Datastone[] call = CallMiner.getMiner().mineAllData(this);
            for (int i = 0; i < call.length; i++) {
                dataManager.queryInsert(call[i]);
            }
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);

        Log.e("Main Activity", year + "-" + month + "-" + date);

        if(year >= 2015 && month >= 11 && date >= 6) {
            btnComplete.setVisibility(View.VISIBLE);
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

    /** 현재 Notification Miner Compat이 서비스에 등록이 되어있는지 확인하는 코드
     *
     * @return 서비스 등록 여부
     */
    private boolean isNotificationMinerCompatStarted() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> list = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        Log.e("Main Activity", "AM -> " + list.toString());
        boolean isOn = false;
        for(int i = 0; i < list.size(); i++) {
            Log.e("Main Activity", "AM -> " + list.get(i).getId() + ", " + this.getPackageName());
            isOn = list.get(i).getId().contains(this.getPackageName());
        }
        return isOn;
    }

    //--------------onCreate 완료--------------//

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notiMaker.closeAllNotification();
        dataManager.close();
    }

    //Activity에서 신호를 받을 때 작동

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GET_BASIC_INFO) {
            if(resultCode == RESULT_OK) {
                dataManager.queryInsert(data, iAgreement);
            } else {
                if(dataManager.isExperimentInfoRecorded()) {
                    Intent intentExp = new Intent(this, BasicInfo.class);
                    startActivityForResult(intentExp, REQUEST_GET_BASIC_INFO);
                }
            }
        } else if(requestCode == REQUEST_GET_PERSONAL_INFOMATION_AGREEMENT) {
            if(resultCode == RESULT_OK) {
                iAgreement = data.getIntExtra("AgreementType", 0);
                Intent intentExp = new Intent(this, BasicInfo.class);
                startActivityForResult(intentExp, REQUEST_GET_BASIC_INFO);
            } else {
                if(dataManager.isExperimentInfoRecorded()) {
                    Intent intentExp = new Intent(this, InfoAgreement.class);
                    startActivityForResult(intentExp, REQUEST_GET_PERSONAL_INFOMATION_AGREEMENT);
                }
            }
        } else if(requestCode == REQUEST_SERVICE) {
            Log.e("Main Activity", "Service End");
            // TODO: 2015-12-02 추후 서비스 개발 완료되면 메인 액티비티 종료 코드를 추가할 것
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Execute 버튼 클릭 이벤트
    public void Execute_Click(View view) {
        //String str = dataManager.showAllData();
        dataManager.exportDatabase();
        //textView.setText(str);
        sendBroadcast(new Intent("lab.u2xd.socialspace.miner.PHONELOG"));
    }

    //Notigen 버튼 클릭 이벤트, 개인정보 재입력 기능으로 변환함
    public void NotiGen_Click(View view) {
        Intent intentExp = new Intent(this, InfoAgreement.class);
        startActivityForResult(intentExp, REQUEST_GET_PERSONAL_INFOMATION_AGREEMENT);
    }

    /**데이터 삭제 버튼 클릭 이벤트
     *
     * @param view 사용자가 클릭한 Button View
     */
    public void DataDelete_Click(View view) {
        dataManager.reset();
        dataManager.querySettingInitializedRemove();
    }

    /** 전화, 문자 읽기 기능 실행 버튼 클릭 이벤트
     *
     * @param view 사용자가 클릭한 Button View
     */
    public void ReadLog_Click(View view) {
        Datastone[] call = CallMiner.getMiner().mineAllData(this);
        // TODO: 2015-11-23 추후에는 문자는 데이터 업데이트 형식으로 읽을 것
        for(int i = 0; i < call.length; i++) {
            dataManager.queryInsert(call[i]);
        }
        bar.setVisibility(View.VISIBLE);
    }

    public void Input_Click(View view) {
        bar.setVisibility(View.INVISIBLE);
        if(textInputBox.getText().toString().equals("uxproject")) {
            for (int i = 0; i < listView.size(); i++) {
                listView.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    public void Complete_Click(View view) {
        /* 폐기된 설문조사
        Intent intentExp = new Intent(this, FinalQuestionaire.class);
        startActivity(intentExp);
        //*/

        Datastone[] sms = SMSMiner.getMiner(this).mineAllData(this);
        // TODO: 2015-11-23 추후에는 문자는 데이터 업데이트 형식으로 읽을 것
        for(int i = 0; i < sms.length; i++) {
            dataManager.queryInsert(sms[i]);
        }
        bar.setVisibility(View.VISIBLE);
    }

    public void RunService_Click(View view) {
        // TODO: 2015-12-02 SpaceField 개발이 완료되면 메인화면(Space Main)을 부를 수 있도록 클래스 명을 변경할 것
        //*
        Intent intentExp = new Intent(this, SpaceField.class);
        startActivityForResult(intentExp, REQUEST_SERVICE);
        //*/
    }

    @Override
    public void onFinish_Query() {
        bar.setVisibility(View.INVISIBLE);
    }
}

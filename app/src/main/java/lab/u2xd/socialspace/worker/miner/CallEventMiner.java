package lab.u2xd.socialspace.worker.miner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/**
 * Created by ysb on 2015-10-12.
 */
public class CallEventMiner extends Service {

    private CallEventPickaxe pickaxe;

    @Override
    public void onCreate() {
        pickaxe = new CallEventPickaxe(this);
        ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(pickaxe, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

class CallEventPickaxe extends PhoneStateListener {

    private Context context;
    private DataManager dbManager;

    private boolean isCallEvent = false;

    public CallEventPickaxe(Context context) {
        this.context = context.getApplicationContext();
        dbManager = DataManager.getManager(this.context);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case 0:     //아무 통화가 없는 상태, 전화 끊음
                if(isCallEvent) {
                    CallMiner.getMiner().mineLatestData(context);
                }
                isCallEvent = false;
                break;
            case 1:     //상대방 전화
                isCallEvent = true;
                break;
            case 2:     //나의 전화
                isCallEvent = true;
                break;
            default:
                break;
        }
        Log.e("Call Event Miner","I got call " + state + " : " + incomingNumber);
    }
}

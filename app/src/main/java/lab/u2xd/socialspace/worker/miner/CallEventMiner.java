package lab.u2xd.socialspace.worker.miner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by ysb on 2015-10-12.
 */
public class CallEventMiner extends Service {

    // TODO: 2015-10-23 실시간으로 전화 수신 및 문자 수신 이벤트 저장 기능 추가할 것
    private CallEventPickaxe pickaxe;

    @Override
    public void onCreate() {
        pickaxe = new CallEventPickaxe();
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
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case 0:     //아무 통화가 없는 상태, 전화 끊음
                break;
            case 1:     //전화 수신
                break;
            case 2:     //통화 중
                break;
            default:
                break;
        }
        Log.e("Call Event Miner","I got call " + state + " : " + incomingNumber);
    }
}

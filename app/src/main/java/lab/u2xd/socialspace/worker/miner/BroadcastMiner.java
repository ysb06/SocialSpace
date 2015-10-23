package lab.u2xd.socialspace.worker.miner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import lab.u2xd.socialspace.worker.warehouse.DataManager;

/** 통화, 문자 등 휴대폰 내 이벤트 발생 감지 및 처리
 * Created by ysb on 2015-10-06.
 */
public class BroadcastMiner extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("Broadcast Miner", action);
        if(action.equals("lab.u2xd.socialspace.miner.PHONELOG")) {
            Toast.makeText(context, "어플에서 보낸 신호를 잡았습니다", Toast.LENGTH_SHORT).show();
        } else if(action.equals("android.intent.action.PHONE_STATE")) {
            Toast.makeText(context, "전화에 관련된 신호를 잡습니다", Toast.LENGTH_SHORT).show();
        } else if(action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.w("Broadcast Miner","Booting Complete");
            Log.w("Broadcast Miner","I wake Call Event Miner");
            context.startService(new Intent(context, CallEventMiner.class));
        } else {
            Toast.makeText(context, "무언가 신호를 잡습니다", Toast.LENGTH_SHORT).show();
        }
        //DataManager.getManager(context).queryInsert(null);
    }
}

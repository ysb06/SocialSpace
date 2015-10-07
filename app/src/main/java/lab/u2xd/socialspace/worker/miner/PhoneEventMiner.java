package lab.u2xd.socialspace.worker.miner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/** 통화, 문자 등 휴대폰 내 이벤트 발생 감지 및 처리
 * Created by ysb on 2015-10-06.
 */
public class PhoneEventMiner extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("lab.u2xd.socialspace.miner.phonelog")) {
            Toast.makeText(context, "어플에서 보낸 신호를 잡았습니다", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "무언가 신호를 잡습니다", Toast.LENGTH_SHORT).show();
        }
    }

}

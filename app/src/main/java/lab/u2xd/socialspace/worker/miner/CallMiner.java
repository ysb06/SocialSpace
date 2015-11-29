package lab.u2xd.socialspace.worker.miner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import lab.u2xd.socialspace.worker.miner.objects.LatestDataPickaxe;
import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/**
 * Created by yim on 2015-10-01.
 */
public class CallMiner extends PhoneLogMiner {

    private static CallMiner object;

    private CallMiner() {
        super(URI_CALL, CALL_PROJECTION, DATE_SORT_ORDER);
    }

    public static CallMiner getMiner() {
        if(object == null) {
            object = new CallMiner();
        }
        return object;
    }

    @Override
    protected void drillDatamine() {
        Log.e("PhoneLogMiner", "Call Reading Complete : " + contentUri.toString() + ", " + sortOrder);

        if(curBasic.moveToFirst()) {
            for(int i = 0; i < curBasic.getCount(); i++) {

                listdata.add(getCurrentDatastone());
                curBasic.moveToNext();
            }
        }
    }

    /** 가장 최신의 통화 목록을 데이터베이스에 저장 */
    protected void mineLatestData(Context context) {
        Thread thread = new Thread(new LatestDataPickaxe(context, curBasic, contentUri, qeuryProjection, sortOrder));
        thread.start();
    }   //프로그래밍 구조적으로 좋아보이진 않지만 기능 구현에 초점을 맞추고 더 건드리지는 않겠음

    private Datastone getCurrentDatastone() {
        Datastone datastone = new Datastone();

        int iType = curBasic.getInt(5);
        String sType = "";
        switch (iType) {
            case 1:
                sType = "받기 Receive";
                break;
            case 2:
                sType = "걸기 Call";
                break;
            case 3:
                sType = "거절 Reject";
                break;
            default:
                sType = "오류 Error " + iType;
                break;
        }
        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_CALL);
        datastone.put(DataManager.FIELD_AGENT, curBasic.getString(2));
        datastone.put(DataManager.FIELD_TARGET, "Me");
        datastone.put(DataManager.FIELD_TIME, curBasic.getLong(3));
        datastone.put(DataManager.FIELD_CONTENT, "통화 종류: " + sType);

        return datastone;
    }
}
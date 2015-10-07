package lab.u2xd.socialspace.worker.miner;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;

import lab.u2xd.socialspace.worker.object.RefinedData;
import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.Datastone;

/**
 * Created by yim on 2015-10-01.
 */
public class SMSMiner extends PhoneLogMiner {

    private static SMSMiner object;

    private HashMap<Integer, String> listContact;

    public static SMSMiner getMiner() {
        if(object == null) {
            object = new SMSMiner();
        }
        return object;
    }

    public SMSMiner() {
        super(PhoneLogMiner.URI_SMS, PhoneLogMiner.SMS_PROJECTION, PhoneLogMiner.DEFAULT_SORT_ORDER);
    }

    // TODO: 2015-10-07 ⓐ 휴대폰 연락처들을 읽어 ⓑ 전화번호를 저장된 이름으로 변경해서 ⓒ 데이터베이스에 저장되도록 할 것

    @Override
    protected void drillDatamine() {
        curBasic = context.getContentResolver().query(contentUri, qeuryProjection, null, null, sortOrder);
        Log.e("SMS Miner", "SMS Reading Complete : " + contentUri.toString() + ", " + sortOrder);
        listdata.clear();

        if(curBasic.moveToFirst()) {
            for(int i = 0; i < curBasic.getCount(); i++) {
                Datastone datastone = new Datastone();

                int iType = curBasic.getInt(5);

                String sFrom = "";
                String sTo = "";
                if(iType == 2) {
                    sFrom = "Me";
                    sTo = curBasic.getString(2);
                } else {
                    sFrom = curBasic.getString(2);
                    sTo = "Me";
                }

                datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_SMS);
                datastone.put(DataManager.FIELD_AGENT, sFrom);
                datastone.put(DataManager.FIELD_TARGET, sTo);
                datastone.put(DataManager.FIELD_TIME, System.currentTimeMillis());
                datastone.put(DataManager.FIELD_CONTENT, "문자 종류: " + iType);

                listdata.add(datastone);

                curBasic.moveToNext();
            }
        }
    }
}

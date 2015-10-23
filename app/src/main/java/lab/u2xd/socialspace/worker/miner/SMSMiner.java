package lab.u2xd.socialspace.worker.miner;

import android.content.Context;
import android.util.Log;

import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/**
 * Created by yim on 2015-10-01.
 */
public class SMSMiner extends PhoneLogMiner {

    private static SMSMiner object;
    private DataManager dataManager;

    public static SMSMiner getMiner(Context context) {
        if(object == null) {
            object = new SMSMiner(context);
        }
        return object;
    }

    public SMSMiner(Context context) {
        super(URI_SMS, PhoneLogMiner.SMS_PROJECTION, PhoneLogMiner.DATE_SORT_ORDER);
        dataManager = DataManager.getManager(context);
    }

    // TODO: 2015-10-07 ⓐ 휴대폰 연락처들을 읽어 ⓑ 전화번호를 저장된 이름으로 변경해서 ⓒ 데이터베이스에 저장되도록 할 것

    @Override
    protected void drillDatamine() {
        Log.e("SMS Miner", "SMS Reading Complete : " + contentUri.toString() + ", " + sortOrder);

        if(curBasic.moveToFirst()) {
            for(int i = 0; i < curBasic.getCount(); i++) {
                Datastone datastone = new Datastone();

                int iType = curBasic.getInt(5);

                String sFrom = "";
                String sTo = "";
                if(iType == 2) {
                    sFrom = "Me";
                    sTo = dataManager.getNameOfNumber(curBasic.getString(2));
                } else {
                    sFrom = dataManager.getNameOfNumber(curBasic.getString(2));
                    sTo = "Me";
                }

                datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_SMS);
                datastone.put(DataManager.FIELD_AGENT, sFrom);
                datastone.put(DataManager.FIELD_TARGET, sTo);
                datastone.put(DataManager.FIELD_TIME, curBasic.getLong(3));
                datastone.put(DataManager.FIELD_CONTENT, "문자 종류: " + iType);

                listdata.add(datastone);

                curBasic.moveToNext();
            }
        }
    }
}

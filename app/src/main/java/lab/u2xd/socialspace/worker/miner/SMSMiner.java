package lab.u2xd.socialspace.worker.miner;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

    @Override
    protected void drillDatamine() {
        Log.e("SMS Miner", "SMS Reading Complete : " + contentUri.toString() + ", " + sortOrder);

        if(curBasic.moveToFirst()) {
            for(int i = 0; i < curBasic.getCount(); i++) {
                listdata.add(extractCurrentDatastone());
                curBasic.moveToNext();
            }
        }
    }

    private Datastone extractCurrentDatastone() {
        Datastone datastone = new Datastone();

        int iType = curBasic.getInt(5);

        String sType = "";
        switch (iType) {
            case 1:
                sType = "받기 Receive";
                break;
            case 2:
                sType = "보내기 Send";
                break;
            default:
                sType = "오류 Error " + iType;
                break;
        }

        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_SMS);
        datastone.put(DataManager.FIELD_AGENT, dataManager.getNameOfNumber(curBasic.getString(2)));
        datastone.put(DataManager.FIELD_TARGET, "Me");
        datastone.put(DataManager.FIELD_TIME, curBasic.getLong(3));
        datastone.put(DataManager.FIELD_CONTENT, "문자 종류: " + sType + ", 길이: " + curBasic.getString(5).length());

        return datastone;
    }
}

package lab.u2xd.socialspace.worker.miner;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;

import lab.u2xd.socialspace.worker.object.RefinedData;

/**
 * Created by yim on 2015-10-01.
 */
public class SMSMiner extends PhoneLogMiner {

    private HashMap<Integer, String> listContact;

    public SMSMiner(Context context) {
        super(PhoneLogMiner.URI_SMS, PhoneLogMiner.SMS_PROJECTION, PhoneLogMiner.DEFAULT_SORT_ORDER);

        listContact = new HashMap<>();

        Cursor contacts = context.getContentResolver().query(URI_CONTACT, CONTACT_PROJECTION, null, null, null);
        if(contacts.moveToFirst()) {
            do {
                listContact.put(contacts.getInt(0), contacts.getString(1));
            } while (contacts.moveToNext());
        }
        contacts.close();
    }

    @Override
    protected void getAllLog(boolean isIndependent) {
        curBasic = context.getContentResolver().query(contentUri, qeuryProjection, null, null, sortOrder);
        Log.e("PhoneLogMiner", "Call Reading Complete : " + contentUri.toString() + ", " + sortOrder);
        listQueriedResult.clear();

        if(curBasic.moveToFirst()) {
            for(int i = 0; i < curBasic.getCount(); i++) {
                Integer smstype = curBasic.getInt(5);
                RefinedData data = new RefinedData("SMS", listContact.get(curBasic.getInt(2)), smstype.toString());
                //RefinedData data = new RefinedData("SMS", curBasic.getString(2), smstype.toString());

                if(smstype == 2) {
                    String temp = data.Agent;
                    data.Agent = data.Target;
                    data.Target = temp;
                }
                data.Time = curBasic.getLong(3);

                listQueriedResult.add(data);
                curBasic.moveToNext();
            }
        }
        if(isIndependent) {
            callback.onQuery_Data(listQueriedResult.toArray(new RefinedData[0]));
        }
    }
}

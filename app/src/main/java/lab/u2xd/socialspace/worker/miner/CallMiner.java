package lab.u2xd.socialspace.worker.miner;

import android.util.Log;

import lab.u2xd.socialspace.worker.object.RefinedData;

/**
 * Created by yim on 2015-10-01.
 */
public class CallMiner extends PhoneLogMiner {

    public CallMiner() {
        super(PhoneLogMiner.URI_CALL, PhoneLogMiner.CALL_PROJECTION, PhoneLogMiner.DEFAULT_SORT_ORDER);
    }

    @Override
    protected void getAllLog(boolean isIndependent) {
        curBasic = context.getContentResolver().query(contentUri, qeuryProjection, null, null, sortOrder);
        Log.e("PhoneLogMiner", "Call Reading Complete : " + contentUri.toString() + ", " + sortOrder);
        listQueriedResult.clear();

        if(curBasic.moveToFirst()) {
            for(int i = 0; i < curBasic.getCount(); i++) {
                Integer phonecalltype = curBasic.getInt(5);
                RefinedData data = new RefinedData("Call", curBasic.getString(2), phonecalltype.toString());

                if(phonecalltype == 2) {
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

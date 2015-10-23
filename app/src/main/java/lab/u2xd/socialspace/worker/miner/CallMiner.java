package lab.u2xd.socialspace.worker.miner;

import android.util.Log;

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

                datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_CALL);
                datastone.put(DataManager.FIELD_AGENT, sFrom);
                datastone.put(DataManager.FIELD_TARGET, sTo);
                datastone.put(DataManager.FIELD_TIME, curBasic.getLong(3));
                datastone.put(DataManager.FIELD_CONTENT, "통화 종류: " + iType);

                listdata.add(datastone);

                curBasic.moveToNext();
            }
        }
    }
}

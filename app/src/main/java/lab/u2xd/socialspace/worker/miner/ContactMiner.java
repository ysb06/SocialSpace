package lab.u2xd.socialspace.worker.miner;

import android.util.Log;

import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/**
 * Created by ysb on 2015-10-12.
 */
public class ContactMiner extends PhoneLogMiner {

    public ContactMiner() {
        super(URI_CONTACT, CONTACT_PROJECTION, NAME_SORT_ORDER);
    }

    @Override
    protected void drillDatamine() {
        Log.e("PhoneLogMiner", "Call Reading Complete : " + contentUri.toString() + ", " + sortOrder);

        if(curBasic.moveToFirst()) {
            for(int i = 0; i < curBasic.getCount(); i++) {
                Datastone datastone = new Datastone();

                datastone.put("Name", curBasic.getString(2));
                datastone.put("Number", curBasic.getString(1).replaceAll("[^0-9]", ""));

                listdata.add(datastone);
                curBasic.moveToNext();
            }
        }
    }
}

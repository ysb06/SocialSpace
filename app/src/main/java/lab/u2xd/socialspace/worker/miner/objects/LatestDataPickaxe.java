package lab.u2xd.socialspace.worker.miner.objects;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

public class LatestDataPickaxe implements Runnable {

    private Context context;
    private Cursor curBasic;
    private Uri contentUri;
    private String[] qeuryProjection;
    private String sortOrder;

    public LatestDataPickaxe(Context context, Cursor cursor, Uri uri, String[] projection, String sort) {
        this.context = context;
        this.curBasic = cursor;
        this.contentUri = uri;
        this.qeuryProjection = projection;
        this.sortOrder = sort;
    }

    @Override
    public void run() {
        Log.e("CallMiner", "Recording Latest Data");
        Log.e("CallMiner", "Waiting 3 Sec");
        try { Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        curBasic = context.getContentResolver().query(contentUri, qeuryProjection, null, null, sortOrder);
        if(curBasic.moveToFirst()) {
            DataManager.getManager(context).queryInsert(getCurrentDatastone());
        } else {
            Log.e("CallMiner", "Failed to recording");
        }
    }

    private Datastone getCurrentDatastone() {
        Datastone datastone = new Datastone();

        int iType = curBasic.getInt(5);

        String sFrom = "Me";
        String sTo = curBasic.getString(2);
        if(sTo == null) {
            sTo = "Unknown";
        }
        if(iType != 2) {
            String temp = sFrom;
            sFrom = sTo;
            sTo = temp;
        }
        datastone.put(DataManager.FIELD_TYPE, DataManager.CONTEXT_TYPE_CALL);
        datastone.put(DataManager.FIELD_AGENT, sFrom);
        datastone.put(DataManager.FIELD_TARGET, sTo);
        datastone.put(DataManager.FIELD_TIME, curBasic.getLong(3));
        datastone.put(DataManager.FIELD_CONTENT, "통화 종류: " + iType);

        return datastone;
    }
}


package lab.u2xd.socialspace.worker.miner.object;

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
        datastone.put(DataManager.FIELD_CONTENT, "통화 종류: " + sType + ", 길이: " + curBasic.getInt(4));

        return datastone;
    }
}


package lab.u2xd.socialspace.worker.miner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;

import lab.u2xd.socialspace.worker.miner.objects.Minable;
import lab.u2xd.socialspace.worker.warehouse.objects.Datastone;

/**
 * Created by ysb on 2015-09-11.
 */
public abstract class PhoneLogMiner implements Runnable {

    final protected static String DATE_SORT_ORDER = "date DESC";
    final protected static String NAME_SORT_ORDER = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

    protected static final Uri URI_CALL = CallLog.Calls.CONTENT_URI;
    protected static final Uri URI_SMS = Uri.parse("content://sms");
    protected static final Uri URI_MMS = Uri.parse("content://mms");
    protected static final Uri URI_CONTACT = Phone.CONTENT_URI;

    final protected static String[] CALL_PROJECTION
            = { CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE };
    final protected static String[] SMS_PROJECTION
            = { Telephony.Sms._ID, Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.DATE_SENT, Telephony.Sms.TYPE };
    final protected static String[] MMS_PROJECTION
            = { Telephony.Mms._ID, Telephony.Mms.THREAD_ID, Telephony.Mms.SUBJECT, Telephony.Mms.DATE, Telephony.Mms.DATE_SENT, Telephony.Mms.MESSAGE_BOX };
    final protected static String[] CONTACT_PROJECTION = {
            Phone.CONTACT_ID, Phone.NUMBER, Phone.DISPLAY_NAME };

    public static final String TYPE_CALL = "Call";
    public static final String TYPE_SMS = "SMS";
    public static final String TYPE_MMS = "MMS";

    protected String[] qeuryProjection;
    protected Uri contentUri;
    protected String sortOrder;

    private Minable callback;

    protected Cursor curBasic;
    protected ArrayList<Datastone> listdata;

    protected PhoneLogMiner(Uri ContentURI, String[] QeuryProjection, String SortOrder) {
        listdata = new ArrayList<>();
        contentUri = ContentURI;
        sortOrder = SortOrder;
        qeuryProjection = QeuryProjection;
    }

    /** 데이터 수집 작업 실행 */
    abstract protected void drillDatamine();

    /** 지정된 위치의 데이터 수집
     *
     * @param context 현재 액티비티 Context
     */
    public Datastone[] mineAllData(Context context) {
        Log.e("PhoneLogMiner", "Data Reading...");
        listdata.clear();
        curBasic = context.getContentResolver().query(contentUri, qeuryProjection, null, null, sortOrder);

        drillDatamine();

        return listdata.toArray(new Datastone[0]);
    }

    /** 지정된 위치의 데이터 쓰레드 독립적으로 수집
     *
     * @param context 현재 액티비티 Context
     * @param callback 쿼리 완료 후 호출할 콜백 함수
     */
    public void mineAllData(Context context, Minable callback) {
        Log.e("PhoneLogMiner", "Data Reading independantly...");
        listdata.clear();
        this.callback = callback;
        curBasic = context.getContentResolver().query(contentUri, qeuryProjection, null, null, sortOrder);

        Thread worker = new Thread(this);
        worker.start();
    }

    @Override
    public void run() {
        drillDatamine();
        callback.onFinish_Request(listdata.toArray(new Datastone[0]));
    }
}

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

import lab.u2xd.socialspace.worker.object.Queryable;
import lab.u2xd.socialspace.worker.object.RefinedData;

/**
 * Created by ysb on 2015-09-11.
 */
public abstract class PhoneLogMiner {

    final public static String DEFAULT_SORT_ORDER = "date DESC";

    final public static Uri URI_CALL = CallLog.Calls.CONTENT_URI;
    final public static Uri URI_SMS = Uri.parse("content://sms");
    final public static Uri URI_MMS = Uri.parse("content://mms");
    final public static Uri URI_CONTACT = Phone.CONTENT_URI;

    final public static String[] CALL_PROJECTION
            = { CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE };
    final public static String[] SMS_PROJECTION
            = { Telephony.Sms._ID, Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.DATE_SENT, Telephony.Sms.TYPE };
    final public static String[] MMS_PROJECTION
            = { Telephony.Mms._ID, Telephony.Mms.THREAD_ID, Telephony.Mms.SUBJECT, Telephony.Mms.DATE, Telephony.Mms.DATE_SENT, Telephony.Mms.MESSAGE_BOX };
    final public static String[] CONTACT_PROJECTION
            = { Phone.NUMBER, Phone.DISPLAY_NAME };

    public static final String TYPE_CALL = "Call";
    public static final String TYPE_SMS = "SMS";
    public static final String TYPE_MMS = "MMS";

    protected String[] qeuryProjection;
    protected Uri contentUri;
    protected String sortOrder;

    protected Context context;

    protected Cursor curBasic;
    protected ArrayList<RefinedData> listQueriedResult;

    protected Queryable callback;

    public PhoneLogMiner(Uri ContentURI, String[] QeuryProjection, String SortOrder) {
        listQueriedResult = new ArrayList<>();
        contentUri = ContentURI;
        sortOrder = SortOrder;
        qeuryProjection = QeuryProjection;
    }

    abstract protected void getAllLog(boolean isIndependent);

    /** 지정된 위치의 데이터 수집
     *
     * @param context 현재 액티비티 Context
     */
    public RefinedData[] queryAllPastData(Context context) {
        Log.e("PhoneLogMiner", "Data Reading...");
        this.context = context;
        getAllLog(false);

        return listQueriedResult.toArray(new RefinedData[0]);
    }

    /** 지정된 위치의 데이터 쓰레드 독립적으로 수집
     *
     * @param context 현재 액티비티 Context
     * @param callback 쿼리 완료 후 호출할 콜백 함수
     */
    public void queryAllPastData(Context context, Queryable callback) {
        Log.e("PhoneLogMiner", "Data Reading independantly...");
        this.context = context;
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                getAllLog(true);
            }
        });
        worker.start();
        this.callback = callback;
    }
}

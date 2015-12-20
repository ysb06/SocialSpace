package lab.u2xd.socialspace.worker.processor;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import lab.u2xd.socialspace.worker.warehouse.objects.ProfileRaw;

/**
 * Created by ysb on 2015-12-07.
 */
public class WeightedACOProcessor extends Processor {

    public static final float IMPACT_TIME = 0.999999f;

    public static final float WEIGHT_CALL = 0.448f;
    public static final float WEIGHT_SMS = 0.240f;
    public static final float WEIGHT_KAKAO = 0.024f;
    public static final float WEIGHT_FACEBOOK = 0.144f;
    public static final float WEIGHT_TWITTER = 0.064f;
    public static final float WEIGHT_OTHERS = 0.080f;

    public static final int TYPE_CALL = 0;
    public static final int TYPE_SMS = 1;
    public static final int TYPE_KAKAOTALK = 2;
    public static final int TYPE_KAKAOSTORY = 3;
    public static final int TYPE_FACEBOOK= 4;
    public static final int TYPE_TWITTER = 5;
    public static final int TYPE_LINE = 6;
    public static final int TYPE_BAND = 7;


    /**
     * 프로세서 생성, 일반적으로 액티비티에서 생성함
     *
     * @param context 현재 액티비티 Context
     */
    public WeightedACOProcessor(Context context) {
        super(context);
    }

    @Override
    protected void calcFriendship(ProfileRaw raw) {
        if(raw.listEvent.size() != raw.listTime.size()) {
            Log.e("WACO Processor", "Warning -> The score is not valid!");
        }
        float fTotalScore = 0;
        long lTimeNow = System.currentTimeMillis();

        long lMaxTime = 0;
        long lMinTime = 9999999999999L;

        ArrayList<Long> listTimeSpan = new ArrayList<>();
        long lPrevTime = 0;

        for(int i = 0; i < raw.listEvent.size(); i++) {
            float weight = 0;
            switch (raw.listEvent.get(i)) {
                case TYPE_CALL:
                    weight = WEIGHT_CALL;
                    break;
                case TYPE_SMS:
                    weight = WEIGHT_SMS;
                    break;
                case TYPE_KAKAOTALK:
                case TYPE_KAKAOSTORY:
                    weight = WEIGHT_KAKAO;
                    break;
                case TYPE_FACEBOOK:
                    weight = WEIGHT_FACEBOOK;
                    break;
                case TYPE_TWITTER:
                    weight = WEIGHT_TWITTER;
                    break;
                case TYPE_LINE:
                case TYPE_BAND:
                    weight = WEIGHT_OTHERS;
                    break;
                default:
                    Log.e("WACO Processor", "Warning -> Unknown Type");
                    break;
            }
            double dTimeSpan = (double)(lTimeNow - raw.listTime.get(i)) / 86400000d;
            fTotalScore += weight * Math.pow(IMPACT_TIME, dTimeSpan);

            lMaxTime = Math.max(lMaxTime, raw.listTime.get(i));
            lMinTime = Math.min(lMinTime, raw.listTime.get(i));

            if(lPrevTime != 0) {
                listTimeSpan.add(raw.listTime.get(i) - lPrevTime);
            }
            lPrevTime = raw.listTime.get(i);
        }

        long lTimeSpanStd = getStandardDeviation(listTimeSpan);
        raw.score = fTotalScore * (lMaxTime - lMinTime) / lTimeSpanStd;
    }

    private long mean(ArrayList<Long> list) {
        if(list.size() <= 0) {
            return 0;
        }
        long mean = 0;

        for(long num : list) {
            mean += num;
        }

        return mean / list.size();
    }

    private long getStandardDeviation(ArrayList<Long> list) {
        double std = 0;
        long mean = mean(list);

        if(list.size() < 2) {
            return -1;
        }

        double diff = 0;
        for(long num : list) {
            diff = num - mean;
            std += diff * diff;
        }
        std = Math.sqrt(std / list.size());

        return (long) std;
    }
}

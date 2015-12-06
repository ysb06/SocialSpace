package lab.u2xd.socialspace.worker.processor;

import android.content.Context;

import lab.u2xd.socialspace.worker.warehouse.objects.ProfileRaw;

/**
 * Created by ysb on 2015-12-07.
 */
public class WeightedProcessor extends Processor {

    private static final float WEIGHT_CALL = WeightedACOProcessor.WEIGHT_CALL;
    private static final float WEIGHT_SMS = WeightedACOProcessor.WEIGHT_SMS;
    private static final float WEIGHT_KAKAO = WeightedACOProcessor.WEIGHT_KAKAO;
    private static final float WEIGHT_FACEBOOK = WeightedACOProcessor.WEIGHT_FACEBOOK;
    private static final float WEIGHT_TWITTER = WeightedACOProcessor.WEIGHT_TWITTER;
    private static final float WEIGHT_OTHERS = WeightedACOProcessor.WEIGHT_OTHERS;

    /**
     * 프로세서 생성, 일반적으로 액티비티에서 생성함
     *
     * @param context 현재 액티비티 Context
     */
    public WeightedProcessor(Context context) {
        super(context);
    }

    @Override
    protected void calcFriendship(ProfileRaw raw) {
        float fTotalScore = 0;

        float fCall = raw.count[0];     //전화
        float fSMS = raw.count[1];      //문자
        float fKakao = raw.count[2] + raw.count[3];     //카카오톡 + 카카오스토리
        float fFacebook = raw.count[4]; //페이스북
        float fTwitter = raw.count[5];  //트위터
        float fOhters = raw.count[6] + raw.count[7];   //기타 (라인 + 밴드)

        fTotalScore = WEIGHT_CALL * fCall + WEIGHT_SMS * fSMS + WEIGHT_KAKAO * fKakao + WEIGHT_FACEBOOK * fFacebook + WEIGHT_TWITTER * fTwitter + WEIGHT_OTHERS * fOhters;

        raw.score = fTotalScore;
    }
}

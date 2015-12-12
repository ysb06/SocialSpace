package lab.u2xd.socialspace.worker.processor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import lab.u2xd.socialspace.R;
import lab.u2xd.socialspace.servicer.object.SocialPlanet;
import lab.u2xd.socialspace.worker.warehouse.DataManager;
import lab.u2xd.socialspace.worker.warehouse.objects.ProfileRaw;

/** 기본 프로세서 방법을 다양화 하기 위해서는 이 클래스를 상속받아 실행하면 됨
 * Created by ysb on 2015-12-04.
 */
public class Processor implements Comparator<ProfileRaw> {

    private Context context;
    private DataManager dbManager;

    private ArrayList<ProfileRaw> rawData;

    /** 프로세서 생성, 일반적으로 액티비티에서 생성함
     *
     * @param context 현재 액티비티 Context
     */
    public Processor(Context context) {
        this.context = context;
        dbManager = DataManager.getManager(context);
        rawData = dbManager.queryContextData();

        //각각 점수 기록
        for(ProfileRaw raw : rawData) {
            calcFriendship(raw);
        }

        removeNotPeople(rawData);

        Collections.sort(rawData, this);
        Collections.reverse(rawData);
        Log.i("Processor", "Calculation Complete -> " + rawData.size());
        for(ProfileRaw raw : rawData) {
            Log.e("Processor", "Score -> " + raw.name + ",  " + raw.score);
        }
    }

    public SocialPlanet[] getPlanets(int planet_count) {
        if(planet_count > 20 || planet_count < 1) {
            planet_count = 20;
            Toast.makeText(context, "표시 수는 20개를 넘을 수 없습니다", Toast.LENGTH_SHORT);
        }
        SocialPlanet[] planets;

        if(rawData.size() < planet_count) {
            planets = new SocialPlanet[rawData.size()];
        } else {
            planets = new SocialPlanet[planet_count];
        }
        Bitmap face = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        for(int i = 0; i < planets.length; i++) {
            //*
            String path = dbManager.queryBitmapPathOfName(rawData.get(i).name);
            if(path == null) {
                planets[i] = new SocialPlanet(face, rawData.get(i));
            } else {
                planets[i] = new SocialPlanet(BitmapFactory.decodeFile(path), rawData.get(i));
            }
            // 실험자들을 위한 코드 비활성화, 실험 후에는 원래대로 할 것 */
            //planets[i] = new SocialPlanet(face, rawData.get(i));        //실험 후에는 삭제
        }
        return planets;
    }

    /** 친밀도 점수 산정 메서드, 단순 카운트. 상속 받아 오버라이드 시켜 수정할 것.
     *
     * @param raw 점수 계산하고 기록할 Raw 객체
     */
    protected void calcFriendship(ProfileRaw raw) {
        int iJustCount = 0;
        for(int n : raw.count) {
            iJustCount += n;
        }
        raw.score = iJustCount;
    }

    //-- 재귀함수가 쓰였으므로 수정 시 주의 할 것 --//
    private void removeNotPeople(ArrayList<ProfileRaw> raws) {
        for(ProfileRaw raw : raws) {
            if(raw.name.equals("카카오톡")) {
                raws.remove(raw);
                removeNotPeople(raws);
                return;
            } else if(raw.name.contains("LINE")) {
                raws.remove(raw);
                removeNotPeople(raws);
                return;
            }
        }
    }

    /** Raw 객체 간 비교를 위한 메서드(Sorting 용), 단순 Score 비교, 만약 더 복잡한 비교를 할 경우 이 부분에 대한 오버라이드 필요
     *
     * @param lhs 비교할 객체 기준
     * @param rhs 비교할 객체
     * @return lhs가 더 클 경우 + (> 0), lhs가 더 작을 경우 - (< 0)
     */
    @Override
    public int compare(ProfileRaw lhs, ProfileRaw rhs) {
        if(lhs.score < rhs.score) {
            return -1;
        } else if(lhs.score == rhs.score) {
            return 0;
        } else {
            return 1;
        }
    }
}

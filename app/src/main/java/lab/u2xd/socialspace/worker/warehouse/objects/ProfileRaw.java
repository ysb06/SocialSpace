package lab.u2xd.socialspace.worker.warehouse.objects;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;

import lab.u2xd.socialspace.worker.warehouse.DataManager;

/**
 * Created by ysb on 2015-12-04.
 */
public class ProfileRaw {
    public String name = "";
    public int[] count;
    public ArrayList<Integer> listEvent;
    public ArrayList<Long> listTime;

    public float score;
    // TODO: 2015-12-04 추후 이벤트에 따른 각각의 시간도 고려해 볼 것

    public ProfileRaw(String name) {
        this.name = name;
        count = new int[DataManager.CONTEXT_TYPE_ALL.length];
        listEvent = new ArrayList<>();
        listTime = new ArrayList<>();
    }

    public boolean checkDataValid() {
        int dataCount = 0;
        for(int n : count) {
            dataCount += n;
        }
        Log.e("Profile Raw Counter", "Count -> " + dataCount + ", Time Intervals -> " + listTime.size());

        if(dataCount == listTime.size()) {
            return true;
        } else {
            return false;
        }
    }
}

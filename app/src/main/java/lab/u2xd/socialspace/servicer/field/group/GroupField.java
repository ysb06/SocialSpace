package lab.u2xd.socialspace.servicer.field.group;

import android.util.Log;

import lab.u2xd.socialspace.servicer.field.SocialPlanet;

/**
 * Created by ysb on 2015-12-03.
 */
public class GroupField implements Runnable {

    private static final int GROUP_COUNT = 9;

    //Closed Field는 Social Field와 운명을 같이 한다.
    protected Thread updator;
    private boolean isLive = true;
    private boolean isPaused = false;

    SocialPlanetGroup[] groups;

    public GroupField() {
        updator = new Thread(this);
    }

    public void initialize(SocialPlanet[] planets) {
        groups = new SocialPlanetGroup[GROUP_COUNT];

        for(int i = 0; i < groups.length; i++) {
            groups[i] = new SocialPlanetGroup();
            groups[i].setPosition(-0.f, 0);
        }

        //그룹 추가 코드, 추후 수정

        //그룹 반지름 설정
        for(int i = 0; i < groups.length; i++) {
            groups[i].setRadius(0.2f + (i * 0.1f));
        }
        groups[0].addPlanet(planets[0]);
        groups[0].addPlanet(planets[1]);
        groups[0].addPlanet(planets[2]);

        updator.start();
    }

    public SocialPlanetGroup[] getGroups() {
        return groups;
    }

    /** 초당 60번 불리는 함수, Closed Field 내 객체 움직임 담당 */
    protected void update() {
        groups[0].update();
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void destroy() {
        isLive = false;
        Log.i("Closed Field", "Closing Closed Field...");
        boolean retry = true;
        while (retry) {
            try {
                updator.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.i("Closed Field", "Interruption Occurred!");
            }
        }
        Log.i("Closed Field", "Closed Closed Field");
    }

    @Override
    public void run() {
        long lUpdateCycleStartTime = 0;
        while (isLive) {
            long lNow = System.currentTimeMillis();
            if(lNow - lUpdateCycleStartTime > 16) {
                lUpdateCycleStartTime = lNow;
                if(isPaused) {

                } else {
                    update();
                }
            }
        }
        Log.i("Closed Field", "Thread Stopped!");
    }
}

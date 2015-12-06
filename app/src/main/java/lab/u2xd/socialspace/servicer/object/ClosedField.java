package lab.u2xd.socialspace.servicer.object;

import android.provider.Settings;
import android.util.Log;

import lab.u2xd.socialspace.servicer.object.SocialPlanet;
import lab.u2xd.socialspace.servicer.object.error.RadiusOutOfBoundException;

/**
 * Created by ysb on 2015-12-03.
 */
public class ClosedField implements Runnable {

    //Closed Field는 Social Field와 운명을 같이 한다.
    private Thread updator;
    private boolean isLive = true;
    private boolean isPaused = false;

    private SocialPlanet[] planets;

    public ClosedField() {
        updator = new Thread(this);
    }

    public void initialize(SocialPlanet[] planets) {
        this.planets = planets;

        //임시 움직임 코드
        float fMinRad = 0.35f;
        float fTotalScore = 0;
        for(SocialPlanet planet : planets) {
            fTotalScore += planet.getScore();
        }


        for(int i = 0; i < planets.length; i++) {
            float ratio = 1 - (planets[i].getScore() / fTotalScore);
            planets[i].setPosition(fMinRad + (ratio * 0.2f) * (float)i);
        }

        //---------------
        // TODO: 2015-12-03 Procssor로부터 표시할 리스트 및 스코어를 받아서 각 객체를 생성 랜더링 준비
        // 이곳은 Processor에서 작업이 끝나면 불려져서 랜더링할 준비를 하게 된다.
        // Processor는 하나가 아니며 어떠한 프로세서가 부르더라도 같은 작업을 할 수 있도록 설게되어야 함.
        // 총 9개(7+_ 법칙에 의해...)의 SocialPlanet을 Processor로부터 받음(SocialPlanet 객체들은 Processor에서 생성)
        // 모든 객체의 움직임 및 UI는 이곳에서 관
        // initialize 작업이 끝나면 SpaceField로 GLPictures 객체들을 넘겨 GLSpaceView에 표시할 수 있게 해야 함
        updator.start();
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

    /** 초당 60번 불리는 함수, Closed Field 내 객체 움직임 담당 */
    private void update() {
        for(int i = 0; i < planets.length; i++) {
            planets[i].update();
        }
    }


}

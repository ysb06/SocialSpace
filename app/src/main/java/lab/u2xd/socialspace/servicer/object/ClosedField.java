package lab.u2xd.socialspace.servicer.object;

import android.util.Log;

import java.util.Random;

import lab.u2xd.socialspace.servicer.graphic.GLCamera;

/**
 * Created by ysb on 2015-12-03.
 */
public class ClosedField implements Runnable {

    //Closed Field는 Social Field와 운명을 같이 한다.
    protected Thread updator;
    private boolean isLive = true;
    private boolean isPaused = false;

    private SocialPlanet[] planets;

    GLCamera camera;
    private float fCenterX = 0;
    private float fCenterY = 0;

    public ClosedField(GLCamera camera) {
        updator = new Thread(this);
        this.camera = camera;
    }

    public void initialize(SocialPlanet[] planets) {
        this.planets = planets;

        float fTotalScore = 0;
        for(SocialPlanet planet : planets) {
            fTotalScore += planet.getScore();
        }

        Random rnd = new Random();

        fCenterX = -0.7f;
        fCenterY = -1.f;

        for(int i = 0; i < planets.length; i++) {
            float ratio = 1 - (planets[i].getScore() / fTotalScore);
            planets[i].setOrbit(2f * ratio - 1.25f + (i * 0.07f));
            planets[i].setCenter(fCenterX, fCenterY);
            planets[i].setPosition(Math.PI / 2 * rnd.nextDouble());
            planets[i].setSpeed(rnd.nextDouble() * 0.001d + 0.001f);
        }
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

    private boolean bSwitch = true;

    /** 초당 60번 불리는 함수, Closed Field 내 객체 움직임 담당 */
    protected void update() {
        for(int i = 0; i < planets.length; i++) {
            if(iMode == 0) {
                if (planets[i].getX() < -0.7f) {
                    planets[i].setPosition(0);
                }
            } else if(iMode == 1) {
                if (planets[i].getX() < -1.0f) {
                    if(i <= 3)
                        planets[i].setPosition(-0.6f);
                    else
                        planets[i].setPosition(0.3f);
                }
            } else if(iMode == 2) {
                if (planets[i].getX() < -0.7f) {
                    planets[i].setPosition(1);
                }
            } else if(iMode == 3) {
                if (planets[i].getX() < -0.5f) {
                    planets[i].setPosition(0.4);
                }
            }
            planets[i].update();
        }
    }

    private int iMode = 0;

    public void onTouch(float x, float y) {
        Log.e("Closed Field", "X -> " + x + ", Y -> " + y);

        if(x > 0.4f && y > 0.5f) {
            iMode++;

            if(iMode > 3) {
                iMode = 0;
            } else if(iMode < 0) {
                iMode = 3;
            }

            if(iMode == 0) {
                camera.requestNormal();

                for(int i = 0; i < planets.length; i++) {
                    planets[i].setVisible(true);
                }
            } else if(iMode == 1) {
                camera.requestFocusNearCenter();

            } else if(iMode == 2) {
                camera.requestMove1();

                if(planets.length >= 4) {
                    for(int i = 0; i < planets.length; i++) {
                        if(i < 4 || i > 11)
                            planets[i].setVisible(false);
                    }
                }
            } else if(iMode == 3) {
                camera.requestMove2();

                if(planets.length >= 11) {
                    for(int i = 0; i < planets.length; i++) {
                        if(i <= 11)
                            planets[i].setVisible(false);
                        else
                            planets[i].setVisible(true);
                    }
                }
            }
            Log.e("Closed Field", "Mode -> " + iMode + ", Size => " + planets[1].getRadius());
        }
    }
}

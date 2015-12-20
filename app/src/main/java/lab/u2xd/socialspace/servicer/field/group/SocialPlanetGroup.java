package lab.u2xd.socialspace.servicer.field.group;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.servicer.graphic.object.GLLineCircle;
import lab.u2xd.socialspace.servicer.graphic.object.component.GLDrawable;
import lab.u2xd.socialspace.servicer.field.SocialPlanet;

/**
 * Created by ysb on 2015-12-09.
 */
public class SocialPlanetGroup implements GLDrawable {

    private ArrayList<SocialPlanet> planets;
    private GLLineCircle track;

    private float fCenterX = 0;
    private float fCenterY = 0;
    private float fRadius = 0;
    private double dRadian = 0;
    /** 1/60초에 변하는 속도 */
    private double dRadianSpeed = 0.002d;

    public SocialPlanetGroup() {
        planets = new ArrayList<>();
        track = new GLLineCircle(0, 0, fRadius);
    }

    public void setRadius(float radius) {
        fRadius = radius;
        track.setRadius(radius);
    }

    public void setPosition(float x, float y) {
        track.setPosition(x, y);
        for(SocialPlanet planet : planets) {
            planet.setCenter(x, y);
        }
        fCenterX = x;
        fCenterY = y;
    }

    public void update() {
        dRadian += dRadianSpeed;

        float fx = (float)(Math.cos(dRadian)) * fRadius + fCenterX;
        float fy = (float)(Math.sin(dRadian)) * fRadius + fCenterY;

        for(SocialPlanet planet : planets) {
            planet.setCenter(fx, fy);
        }

        for(SocialPlanet planet : planets) {
            planet.update();
        }
    }

    public void addPlanet(SocialPlanet planet) {
        planets.add(planet);
        planet.setOrbit(0.15f);

        double frag = 2 * Math.PI / (double)planets.size();
        for (int i = 0; i < planets.size(); i++) {
            planets.get(i).setPosition(frag * (double)i);
        }
    }

    @Override
    public void onCreate(GL10 gl) {
        for(SocialPlanet planet : planets) {
            planet.onCreate(gl);
        }
    }

    @Override
    public void onDestroy() {
        for(SocialPlanet planet : planets) {
            planet.onDestroy();
        }
    }

    @Override
    public void onDraw(GL10 gl) {
        for(SocialPlanet planet : planets) {
            planet.onDraw(gl);
        }
        track.onDraw(gl);
    }
}

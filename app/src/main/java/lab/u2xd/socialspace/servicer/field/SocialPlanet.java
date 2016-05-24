package lab.u2xd.socialspace.servicer.field;

import android.graphics.Bitmap;
import android.graphics.Color;

import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.supporter.graphic.glegacy.object.GLLineCircle;
import lab.u2xd.socialspace.supporter.graphic.glegacy.object.component.GLDrawable;
import lab.u2xd.socialspace.supporter.graphic.glegacy.object.GLPicture;
import lab.u2xd.socialspace.supporter.graphic.glegacy.object.GLText;
import lab.u2xd.socialspace.worker.warehouse.objects.ProfileRaw;

/**
 * Created by ysb on 2015-12-03.
 */
public class SocialPlanet implements GLDrawable {

    private float fCenterX = 0;
    private float fCenterY = 0;

    private GLText name;
    private GLPicture face;
    private GLLineCircle track;

    private float score;

    private float fRadius = 0;
    private double dRadian = 0;
    /** 1/60초에 변하는 속도 */
    private double dRadianSpeed = 0.002d;

    //사진의 크기는 0.15f로 통일
    //글자 크기는 조금 더 넓혀 0.2f로 통일
    public SocialPlanet(Bitmap profile, ProfileRaw raw) {
        face = new GLPicture(0, 0, 0.15f, profile);
        this.name = new GLText(0, 0, 0.03f, Color.WHITE, raw.name);
        track = new GLLineCircle(fCenterX, fCenterY, 1);
        score = raw.score;
    }

    public void setPosition(double radian) {
        dRadian = radian;
    }

    @Override
    public void onCreate(GL10 gl) {
        name.onCreate(gl);
        face.onCreate(gl);
        track.onCreate(gl);
    }

    public float getX() {
        return face.getX();
    }

    public float getCenterX() {
        return fCenterX;
    }

    public float getCenterY() {
        return fCenterY;
    }

    public float getY() {
        return face.getY();
    }

    public void resetPosition() {
        dRadian = 0f;
    }

    public void setSpeed(double speed) {
        dRadianSpeed = speed;
    }

    /** 반지름을 설정, 중심은 화면 왼쪽 최하단 <b>근처</b>
     *
     * @param radius 0에서 1사이의 반지름 값
     */
    public void setOrbit(float radius) {
        fRadius = radius;
        track.setRadius(radius);
    }

    public float getRadius() {
        return fRadius;
    }

    public float getScore() {
        return score;
    }

    public void update() {
        dRadian += dRadianSpeed;

        float fx = (float)(Math.cos(dRadian)) * fRadius + fCenterX;
        float fy = (float)(Math.sin(dRadian)) * fRadius + fCenterY;

        face.setPosition(fx, fy);
        name.setPosition(fx, fy - (face.getHeight() + name.getHeight()) / 2);
    }

    public void setCenter(float x, float y) {
        fCenterX = x;
        fCenterY = y;
        track.setPosition(x, y);
    }

    public void setVisible(boolean visible) {
        name.setVisible(visible);
        face.setVisible(visible);
        track.setVisible(visible);
    }

    @Override
    public void onDraw(GL10 gl) {
        face.onDraw(gl);
        name.onDraw(gl);
        track.onDraw(gl);
    }

    @Override
    public void onDestroy() {
        face.onDestroy();
        name.onDestroy();
    }
}

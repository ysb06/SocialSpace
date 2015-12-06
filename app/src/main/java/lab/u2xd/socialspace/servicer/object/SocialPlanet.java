package lab.u2xd.socialspace.servicer.object;

import android.graphics.Bitmap;
import android.graphics.Color;

import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.servicer.graphic.object.GLLineCircle;
import lab.u2xd.socialspace.servicer.graphic.object.component.GLDestroyable;
import lab.u2xd.socialspace.servicer.graphic.object.component.GLDrawable;
import lab.u2xd.socialspace.servicer.graphic.object.GLPicture;
import lab.u2xd.socialspace.servicer.graphic.object.GLText;
import lab.u2xd.socialspace.worker.warehouse.objects.ProfileRaw;

/**
 * Created by ysb on 2015-12-03.
 */
public class SocialPlanet implements GLDrawable, GLDestroyable {

    private static final float CENTER_X = 0.7f;
    private static final float CENTER_Y = 1f;

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
        track = new GLLineCircle(-CENTER_X, -CENTER_Y, 1);
        score = raw.score;
    }

    private void setPosition(float x, float y) {
        face.setPosition(x, y);
        name.setPosition(x, y - (face.getHeight() + name.getTextPicture().getHeight()) / 2);
    }

    /** 반지름을 설정, 중심은 화면 왼쪽 최하단 <b>근처</b>
     *
     * @param radius 0에서 1사이의 반지름 값
     */
    public void setPosition(float radius) {
        fRadius = radius;
        track.setRadius(radius);
    }

    public GLText getName() {
        return name;
    }

    public GLPicture getFace() {
        return face;
    }

    public GLLineCircle getTrack() {
        return track;
    }

    public float getScore() {
        return score;
    }

    public void update() {
        dRadian += dRadianSpeed;
        if(face.getX() < -(name.getTextPicture().getWidth() / 2) - 0.6f) {
            dRadian = 0;
        }
        rePosition(dRadian);
    }

    private void rePosition(double radian) {
        float fx = (float)(Math.cos(radian)) * fRadius - CENTER_X;
        float fy = (float)(Math.sin(radian)) * fRadius - CENTER_Y;

        setPosition(fx, fy);
    }

    @Override
    public void draw(GL10 gl) {
        face.draw(gl);
        name.draw(gl);
        track.draw(gl);
    }

    @Override
    public void onDestroy() {
        face.onDestroy();
        name.onDestroy();
    }
}

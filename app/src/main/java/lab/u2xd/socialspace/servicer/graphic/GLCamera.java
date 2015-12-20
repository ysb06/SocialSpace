package lab.u2xd.socialspace.servicer.graphic;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ysb on 2015-12-10.
 */
public class GLCamera {

    private static final float CAM_SPEED = 0.01f;
    private static final float CAM_ZOOM_SPEED = 0.005f;

    private static final int MODE_NO_WORK = 0;
    private static final int MODE_NORMAL = 1;
    private static final int MODE_CENTER = 2;

    private int iMode = 0;

    public GLCamera() {

    }

    private float targetX = 0;
    private float targetY = 0;
    int iZoomRequest = 0;

    private boolean isRequestedNormal = false;

    public void setup(GL10 gl) {
        gl.glPopMatrix();
        gl.glPushMatrix();
    }

    public void controlView(GL10 gl) {

        if(isRequestedNormal) {
            gl.glPopMatrix();
            gl.glPushMatrix();
            isRequestedNormal = false;
        }

        if(iZoomRequest == 1) {
            gl.glScalef(1.45f, 1.45f, 1.45f);
            iZoomRequest = 0;
        }

        runCamWorkMove(gl);
    }

    public void requestFocusNearCenter() {
        targetX = 0.5f;
        targetY = 0.5f;
        iZoomRequest = 1;
    }

    public void requestMove1() {
        targetX = -0.3f;
        targetY = -0.3f;
    }

    public void requestMove2() {
        targetX = -0.6f;
        targetY = -0.6f;
    }

    public void requestNormal() {
        isRequestedNormal = true;
    }

    private void runCamWorkMove(GL10 gl) {

        if(targetX < -0.001 || targetX > 0.001) {
            if(targetX >= 0) {
                targetX -= CAM_SPEED;
                gl.glTranslatef(CAM_SPEED, 0, 0);
            } else {
                targetX += CAM_SPEED;
                gl.glTranslatef(-CAM_SPEED, -0, 0);
            }
        } else {
            targetX = 0;
        }

        if(targetY < -0.001 || targetY > 0.001) {
            if(targetY >= 0) {
                targetY -= CAM_SPEED;
                gl.glTranslatef(0, CAM_SPEED, 0);
            } else {
                targetY += CAM_SPEED;
                gl.glTranslatef(0, -CAM_SPEED, 0);
            }
        } else {
            targetY = 0;
        }
    }

}

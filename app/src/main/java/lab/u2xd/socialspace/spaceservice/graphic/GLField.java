package lab.u2xd.socialspace.spaceservice.graphic;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ysb on 2015-12-02.
 */
public class GLField extends Thread implements GLSurfaceView.Renderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.e("Glass Library", "Created");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e("Glass Library", "Changed");
    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    @Override
    public void run() {

    }
}

package lab.u2xd.socialspace.supporter.graphic.glspace2d;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.supporter.graphic.glspace2d.objects.GSDrawable;

/**
 * Created by ysb on 2016-01-05.
 */
public class GSView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private ArrayList<GSDrawable> objects;

    public GSView(Context context) {
        super(context);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //-- Function Part --//

    /** 개체 추가, 반드시 OnCreate 단계에서만 불려야 함
     *
     * @param object 추가할 사용자 정의 GS 객체
     */
    public void addObject(GSDrawable object) {
        objects.add(object);
    }



    //-- Renderer Part --//

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    public void destroy() {

    }
}

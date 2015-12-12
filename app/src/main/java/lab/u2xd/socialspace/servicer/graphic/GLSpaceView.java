package lab.u2xd.socialspace.servicer.graphic;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.servicer.graphic.object.component.GLDrawable;

/**
 * Created by ysb on 2015-12-03.
 */
public class GLSpaceView extends android.opengl.GLSurfaceView implements GLSurfaceView.Renderer {

    private final float[] COLOR_BASE = {0.05f, 0.05f, 0.05f, 1};
    private ArrayList<GLDrawable> objects;
    private GLCamera camera;

    public GLSpaceView(Context context, GLCamera camera) {
        super(context);
        objects = new ArrayList<>();
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        this.camera = camera;
    }

    public GLSpaceView(Context context, AttributeSet attrs, GLCamera camera) {
        super(context, attrs);
        objects = new ArrayList<>();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderer(this);
        this.camera = camera;
    }

    /** 개체 추가, 반드시 OnCreate 단계에서만 불려야 함
     *
     * @param object 추가할 사용자 정의 GL 객체
     */
    public void addObject(GLDrawable object) {
        objects.add(object);
    }

    public void stopRendering() {
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void startRendering() {
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(COLOR_BASE[0], COLOR_BASE[1], COLOR_BASE[2], COLOR_BASE[3]);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        for(GLDrawable drawable : objects) {
            drawable.onCreate(gl);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(width < height)
            gl.glViewport(-(height - width) / 2, 0, height, height);
        else
            gl.glViewport(0, 0, width, width);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        camera.controlView(gl);

        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).onDraw(gl);
        }
    }

    public void destroy() {
        for(GLDrawable drawable : objects) {
            drawable.onDestroy();
        }
    }
}

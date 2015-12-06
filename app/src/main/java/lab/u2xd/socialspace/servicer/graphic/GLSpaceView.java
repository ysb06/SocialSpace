package lab.u2xd.socialspace.servicer.graphic;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.servicer.graphic.object.component.GLDestroyable;
import lab.u2xd.socialspace.servicer.graphic.object.component.GLDrawable;
import lab.u2xd.socialspace.servicer.graphic.object.GLPicture;
import lab.u2xd.socialspace.servicer.graphic.object.GLText;
import lab.u2xd.socialspace.servicer.object.SocialPlanet;

/**
 * Created by ysb on 2015-12-03.
 */
public class GLSpaceView extends android.opengl.GLSurfaceView implements GLSurfaceView.Renderer, GLDestroyable {

    private final float[] COLOR_BASE = {0.05f, 0.05f, 0.05f, 1};
    private ArrayList<GLDrawable> objects;
    private ArrayList<GLDestroyable> listDestroy;
    private ArrayList<GLPicture> pictures;

    public GLSpaceView(Context context) {
        super(context);
        objects = new ArrayList<>();
        listDestroy = new ArrayList<>();
        pictures = new ArrayList<>();
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public GLSpaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        objects = new ArrayList<>();
        listDestroy = new ArrayList<>();
        pictures = new ArrayList<>();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderer(this);
    }

    /** 개체 추가, 반드시 OnCreate 단계에서만 불려야 함
     *
     * @param picture 추가할 GLPicture 개체
     */
    public void addObject(GLPicture picture) {
        pictures.add(picture);
        objects.add(picture);

        listDestroy.add(picture);
    }

    /** 개체 추가, 반드시 OnCreate 단계에서만 불려야 함
     *
     * @param text 추가할 GLText 개체
     */
    public void addObject(GLText text) {
        pictures.add(text.getTextPicture());
        objects.add(text);

        listDestroy.add(text);
    }

    public void addObject(SocialPlanet picture) {
        addObject(picture.getFace());
        addObject(picture.getName());
        addObject(picture.getTrack());

        listDestroy.add(picture);
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
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        for(GLPicture picture : pictures) {
            picture.initializeTexture(gl);
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

        for(int i = 0; i < objects.size(); i++) {
            objects.get(i).draw(gl);
        }
    }

    @Override
    public void onDestroy() {
        for(GLDestroyable object : listDestroy) {
            object.onDestroy();
        }
    }
}

package lab.u2xd.socialspace.supporter.graphic.glspace2d.objects;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ysb on 2016-01-05.
 */
public interface GSDrawable {
    void onCreate(GL10 gl);
    void onDestroy();
    void onDraw(GL10 gl);
}

package lab.u2xd.socialspace.supporter.graphic.glegacy.object.component;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ysb on 2015-12-03.
 */
public interface GLDrawable {

    void onCreate(GL10 gl);
    void onDestroy();
    void onDraw(GL10 gl);
}

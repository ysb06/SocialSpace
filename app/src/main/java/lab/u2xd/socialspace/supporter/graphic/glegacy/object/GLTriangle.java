package lab.u2xd.socialspace.supporter.graphic.glegacy.object;

import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.supporter.graphic.glegacy.object.component.GLDrawable;
import lab.u2xd.socialspace.supporter.graphic.glegacy.object.component.GLObject2D;

/**
 * Created by ysb on 2015-12-03.
 */
public class GLTriangle extends GLObject2D implements GLDrawable {

    public GLTriangle(float x, float y, float width, float height) {
        super(x, y, width, height);
        setVertexBuffer();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        setVertexBuffer();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setVertexBuffer();
    }

    protected void setVertexBuffer() {
        float[] vertices = {
                fx, fy + fheight / 3 * 2,
                fx - fwidth / 2, fy - fheight / 3,
                fx + fwidth / 2, fy - fheight / 3
        };
        super.setVertexBuffer(vertices);
    }

    @Override
    public void onCreate(GL10 gl) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onDraw(GL10 gl) {
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, verBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

    }
}

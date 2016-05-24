package lab.u2xd.socialspace.supporter.graphic.glegacy.object.component;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by ysb on 2015-12-03.
 */
public class GLObject2D {

    protected float fx = 0;
    protected float fy = 0;
    protected float fwidth = 0;
    protected float fheight = 0;

    protected FloatBuffer verBuffer;

    public GLObject2D(float x, float y, float width, float height) {
        fx = x;
        fy = y;
        fwidth = width;
        fheight = height;
    }

    public void setPosition(float x, float y) {
        fx = x;
        fy = y;
    }

    public float getX() {
        return fx;
    }

    public float getY() {
        return fy;
    }

    public void setSize(float width, float height) {
        fwidth = width;
        fheight = height;
    }

    public float getWidth() {
        return fwidth;
    }

    public float getHeight() {
        return fheight;
    }

    protected void setVertexBuffer(float[] vertices) {
        verBuffer = convertFloatToFloatBuffer(vertices);
    }

    protected FloatBuffer convertFloatToFloatBuffer(float[] vertices) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        buffer.put(vertices);
        buffer.position(0);

        return buffer;
    }

}

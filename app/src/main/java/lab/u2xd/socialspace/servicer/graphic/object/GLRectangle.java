package lab.u2xd.socialspace.servicer.graphic.object;

import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.servicer.graphic.object.component.GLDrawable;
import lab.u2xd.socialspace.servicer.graphic.object.component.GLObject2D;

/**
 * Created by ysb on 2015-12-03.
 */
public class GLRectangle extends GLObject2D implements GLDrawable {

    protected ByteBuffer idxBuffer;
    protected byte[] index = {
            0, 1, 2,
            0, 2, 3
    };

    public GLRectangle(float x, float y, float width, float height) {
        super(x, y, width, height);
        setIndexBuffer();
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
                fx - fwidth / 2, fy + fheight / 2, 0f,
                fx + fwidth / 2, fy + fheight / 2, 0f,
                fx + fwidth / 2, fy - fheight / 2, 0f,
                fx - fwidth / 2, fy - fheight / 2, 0f
        };
        super.setVertexBuffer(vertices);
    }

    private void setIndexBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(index.length);
        buffer.put(index);
        buffer.position(0);

        idxBuffer = buffer;
    }

    @Override
    public void draw(GL10 gl) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, index.length, GL10.GL_UNSIGNED_BYTE, idxBuffer);
    }
}

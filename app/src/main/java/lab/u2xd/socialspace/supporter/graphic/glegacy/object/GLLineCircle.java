package lab.u2xd.socialspace.supporter.graphic.glegacy.object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.supporter.graphic.glegacy.object.component.GLDrawable;

/**
 * Created by ysb on 2015-12-06.
 */
public class GLLineCircle implements GLDrawable {

    protected float fx = 0;
    protected float fy = 0;
    private float fRadius;
    private float fStartAngle;
    private float fAngle;
    protected boolean isVisible = true;

    private FloatBuffer[] buffers;

    private int iDensity = 50;

    /** 원 GL 객체
     *
     * @param x
     * @param y
     * @param radius
     */
    public GLLineCircle(float x, float y, float radius) {
        fx = x;
        fy = y;
        fRadius = radius;
        fStartAngle = 0;
        fAngle = 2 * (float)Math.PI;

        setVertexBuffer();
    }

    /** 호 GL 객체
     *
     * @param x x 위치
     * @param y y 위치
     * @param radius        반지름
     * @param start_angle   시작 각도 (라디안)
     * @param arc_angle     그려질 각도 (라디안)
     */
    public GLLineCircle(float x, float y, float radius, float start_angle, float arc_angle) {
        fx = x;
        fy = y;
        fRadius = radius;
        fStartAngle = start_angle;
        fAngle = arc_angle;

        setVertexBuffer();
    }

    public void setPosition(float x, float y) {
        fx = x;
        fy = y;
        setVertexBuffer();
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public float getX() {
        return fx;
    }

    public float getY() {
        return fy;
    }

    public void setRadius(float radius) {
        fRadius = radius;
        setVertexBuffer();
    }

    public float getRadius() {
        return fRadius;
    }

    protected void setVertexBuffer() {
        float[][] vertices = new float[iDensity][4];
        buffers = new FloatBuffer[iDensity];

        float theta = fAngle / (float)iDensity;//theta is now calculated from the arc angle instead, the - 1 bit comes from the fact that the arc is open
        float tangetial_factor = (float)Math.tan(theta);
        float radial_factor = (float)Math.cos(theta);

        float x = fRadius * (float)Math.cos(fStartAngle);
        float y = fRadius * (float)Math.sin(fStartAngle);

        vertices[0][0] = x + fx;
        vertices[0][1] = y + fy;

        for(int i = 1; i <= iDensity; i++) {
            float tx = -y;
            float ty = x;

            x += tx * tangetial_factor;
            y += ty * tangetial_factor;

            x *= radial_factor;
            y *= radial_factor;

            vertices[i - 1][2] = x + fx;
            vertices[i - 1][3] = y + fy;
            if(i < iDensity) {
                vertices[i][0] = x + fx;
                vertices[i][1] = y + fy;
            }

            buffers[i - 1] = convertFloatToFloatBuffer(vertices[i - 1]);
        }
    }

    protected FloatBuffer convertFloatToFloatBuffer(float[] vertices) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        buffer.put(vertices);
        buffer.position(0);

        return buffer;
    }


    @Override
    public void onCreate(GL10 gl) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onDraw(GL10 gl) {
        if (isVisible) {
            for (FloatBuffer buffer : buffers) {
                gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffer);
                gl.glDrawArrays(GL10.GL_LINES, 0, 2);
            }
        }
    }
}

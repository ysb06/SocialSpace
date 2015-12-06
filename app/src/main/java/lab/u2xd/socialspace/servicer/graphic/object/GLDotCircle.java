package lab.u2xd.socialspace.servicer.graphic.object;

import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.servicer.graphic.object.component.GLDrawable;

/**
 * Created by ysb on 2015-12-06.
 */
public class GLDotCircle implements GLDrawable {

    protected float fx = 0;
    protected float fy = 0;
    private float fRadius;
    private float fStartAngle;
    private float fAngle;

    private int iDensity = 100;

    private GLTriangle[] triLine;

    public GLDotCircle(float x, float y, float radius) {
        fx = x;
        fy = y;
        fRadius = radius;
        fStartAngle = 0;
        fAngle = 2 * (float)Math.PI;

        triLine = new GLTriangle[iDensity];
        for(int i = 0; i < triLine.length; i++) {
            triLine[i] = new GLTriangle(0, 0, 0.01f, 0.01f);
        }
        initialize();
    }

    public GLDotCircle(float x, float y, float radius, float start_angle, float arc_angle) {
        fx = x;
        fy = y;
        fRadius = radius;
        fStartAngle = start_angle;
        fAngle = arc_angle;

        triLine = new GLTriangle[iDensity];

        for(int i = 0; i < triLine.length; i++) {
            triLine[i] = new GLTriangle(0, 0, 0.1f, 0.1f);
        }
        initialize();
    }

    public void setPosition(float x, float y) {
        fx = x;
        fy = y;

        initialize();
    }

    public void setSize(float radius) {
        fRadius = radius;

        initialize();
    }

    private void initialize() {
        float theta = fAngle / (float)iDensity;//theta is now calculated from the arc angle instead, the - 1 bit comes from the fact that the arc is open
        float tangetial_factor = (float)Math.tan(theta);
        float radial_factor = (float)Math.cos(theta);

        float x = fRadius * (float)Math.cos(fStartAngle);
        float y = fRadius * (float)Math.sin(fStartAngle);

        triLine[0].setPosition(x + fx, y + fy);

        for(int i = 1; i < iDensity; i++) {
            float tx = -y;
            float ty = x;

            x += tx * tangetial_factor;
            y += ty * tangetial_factor;

            x *= radial_factor;
            y *= radial_factor;

            triLine[i].setPosition(x + fx, y + fy);
        }
    }

    @Override
    public void draw(GL10 gl) {
        for(GLTriangle triangle : triLine) {
            triangle.draw(gl);
        }
    }
}

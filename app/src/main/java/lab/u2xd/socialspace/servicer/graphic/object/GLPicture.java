package lab.u2xd.socialspace.servicer.graphic.object;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ysb on 2015-12-03.
 */
public class GLPicture extends GLRectangle {

    private Bitmap source;
    private FloatBuffer txrBuffer;
    private int[] texture;

    protected boolean isVisible = true;

    public GLPicture(float x, float y, float size, Bitmap source) {
        super(x, y, size, size);
        this.source = source;
        float[] fTextureIndex = {
                0.01f, 0f,
                1f, 0f,
                1f, 1f,
                0.01f, 1f,
        };
        txrBuffer = convertFloatToFloatBuffer(fTextureIndex);
        texture = new int[1];

        float fSizeRatio = 0;
        float sourceWidth = (float) source.getWidth();
        float sourceHeight = (float) source.getHeight();
        if(sourceWidth > sourceHeight) {
            fSizeRatio = sourceHeight / sourceWidth;
            setSize(size, size * fSizeRatio);
        } else {
            fSizeRatio = sourceWidth / sourceHeight;
            setSize(size * fSizeRatio, size);
        }
    }

    @Override
    public void onCreate(GL10 gl) {
        gl.glGenTextures(1, texture, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, source, 0);
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public void onDraw(GL10 gl) {
        if(isVisible) {
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, txrBuffer);
            gl.glDrawElements(GL10.GL_TRIANGLES, index.length, GL10.GL_UNSIGNED_BYTE, idxBuffer);
            gl.glDisable(GL10.GL_TEXTURE_2D);
        }
    }

    @Override
    public void onDestroy() {
        source.recycle();
    }
}

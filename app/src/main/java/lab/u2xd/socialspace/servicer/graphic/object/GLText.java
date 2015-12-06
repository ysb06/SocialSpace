package lab.u2xd.socialspace.servicer.graphic.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import javax.microedition.khronos.opengles.GL10;

import lab.u2xd.socialspace.servicer.graphic.object.component.GLDestroyable;
import lab.u2xd.socialspace.servicer.graphic.object.component.GLDrawable;

/**
 * Created by ysb on 2015-12-04.
 */
public class GLText implements GLDrawable, GLDestroyable {

    /** 텍스트 선명도, 값이 높을수록 품질은 높으나 속도가 느려질 수 있음 */
    private static final float RESOLUTION_TEXT = 50f;

    private String sContent;
    private int iColor = 0;
    private GLPicture glText;

    private Bitmap bitText;

    public GLText(float x, float y, float size, int color, String text) {
        glText = createStringBitmap(text, color, RESOLUTION_TEXT);
        setPosition(x, y);
        setSize(size);
        sContent = text;
        iColor = color;
    }

    public GLPicture getTextPicture() {
        return glText;
    }

    private GLPicture createStringBitmap(String text, int color, float size) {
        Rect rectBound = new Rect();

        Paint paint = new Paint();
        paint.setTextSize(size);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.LEFT);

        paint.getTextBounds(text, 0, text.length(), rectBound);
        rectBound.set(rectBound.left, rectBound.top, rectBound.right + 5, rectBound.bottom + 10);
        float baseline = -paint.ascent(); // ascent() is negative

        Bitmap surface = Bitmap.createBitmap(rectBound.width(), rectBound.height(), Bitmap.Config.ARGB_8888);
        surface.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(surface);
        canvas.drawText(text, 0, baseline, paint);
        bitText = surface;

        return new GLPicture(0, 0, 1f, surface);
    }

    public void setPosition(float x, float y) {
        glText.setPosition(x, y);
    }

    /** 높이를 기준으로 한 글자 크기 설정
     *
     * @param size 글자 크기
     */
    public void setSize(float size) {
        float ratio = glText.getWidth() / glText.getHeight();
        glText.setSize(size * ratio, size);
    }

    @Override
    public void draw(GL10 gl) {
        glText.draw(gl);
    }

    @Override
    public void onDestroy() {
        bitText.recycle();
        glText.onDestroy();
    }
}

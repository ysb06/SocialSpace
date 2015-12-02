package lab.u2xd.socialspace.spaceservice;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import lab.u2xd.socialspace.R;
import lab.u2xd.socialspace.spaceservice.graphic.GLField;

public class SpaceField extends AppCompatActivity {

    private GLSurfaceView glsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //OpenGL 초기화
        glsv = new GLSurfaceView(this);
        glsv.setRenderer(new GLField());
        glsv.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(glsv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_space_field, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

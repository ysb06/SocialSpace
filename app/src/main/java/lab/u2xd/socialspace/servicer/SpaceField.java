package lab.u2xd.socialspace.servicer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import lab.u2xd.socialspace.R;
import lab.u2xd.socialspace.servicer.graphic.GLSpaceView;
import lab.u2xd.socialspace.servicer.object.ClosedField;
import lab.u2xd.socialspace.servicer.object.SocialPlanet;
import lab.u2xd.socialspace.worker.processor.Processor;
import lab.u2xd.socialspace.worker.processor.WeightedACOProcessor;

public class SpaceField extends AppCompatActivity implements View.OnTouchListener {

    private GLSpaceView glsv;
    private ClosedField field;
    private Processor calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calculator = new Processor(this);
        SocialPlanet[] planets = calculator.getPlanets();

        //OpenGL 초기화
        glsv = new GLSpaceView(this);
        glsv.setOnTouchListener(this);
        for(SocialPlanet planet : planets) {
            glsv.addObject(planet);
        }
        setContentView(glsv);

        field = new ClosedField();
        field.initialize(planets);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_space_field, menu);
        return true;
    }

    @Override
    protected void onPause() {
        glsv.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        glsv.onResume();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        field.destroy();
        glsv.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        glsv.startRendering();
        return false;
    }
}

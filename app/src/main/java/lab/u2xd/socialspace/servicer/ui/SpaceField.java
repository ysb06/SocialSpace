package lab.u2xd.socialspace.servicer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import lab.u2xd.socialspace.R;
import lab.u2xd.socialspace.servicer.graphic.GLCamera;
import lab.u2xd.socialspace.servicer.graphic.GLSpaceView;
import lab.u2xd.socialspace.servicer.field.ClosedField;
import lab.u2xd.socialspace.servicer.field.SocialPlanet;
import lab.u2xd.socialspace.servicer.field.group.GroupField;
import lab.u2xd.socialspace.servicer.field.group.SocialPlanetGroup;
import lab.u2xd.socialspace.worker.processor.Processor;
import lab.u2xd.socialspace.worker.processor.WeightedACOProcessor;

public class SpaceField extends AppCompatActivity implements View.OnTouchListener {

    private static final int MODE_NORMAL = 1;
    private static final int MODE_GROUP = 2;
    private static final int MODE_BIG = 3;

    private GLSpaceView glsv;
    private GLCamera camera;
    private float fDisplayWidth = 0;
    private float fDisplayHeight = 0;

    private float fDispHalfRelativeWidth = 0;

    private Processor calculator;

    private GroupField groupField;
    private ClosedField field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mode = MODE_NORMAL;

        camera = new GLCamera();
        glsv = new GLSpaceView(this, camera);
        glsv.setOnTouchListener(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        fDisplayWidth = (float) dm.widthPixels;
        fDisplayHeight = (dm.heightPixels - getStatusBarSizeOnCreate());

        Log.e("SpaceField", "Display H -> " + fDisplayHeight + ", rect H -> " + getStatusBarSizeOnCreate());
        fDispHalfRelativeWidth = fDisplayWidth / fDisplayHeight;

        setContentView(glsv);

        calculator = new WeightedACOProcessor(this);
        SocialPlanet[] planets = calculator.getPlanets();

        if(mode == MODE_NORMAL) {
            //OpenGL 초기화
            for(SocialPlanet planet : planets) {
                glsv.addObject(planet);
            }

            field = new ClosedField(camera);
            field.initialize(planets);
        } else if(mode == MODE_GROUP) {
            groupField = new GroupField();
            groupField.initialize(planets);

            SocialPlanetGroup[] groups = groupField.getGroups();

            for(SocialPlanetGroup group : groups) {
                glsv.addObject(group);
            }
        } else if(mode == MODE_BIG) {
            for(SocialPlanet planet : planets) {
                glsv.addObject(planet);
            }
            field = new ClosedField(camera);
            field.initialize(planets);
        }
        glsv.startRendering();
    }

    private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
    private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
    private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;

    private float getStatusBarSizeOnCreate(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);

        int statusBarHeight;

        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_HIGH:
                statusBarHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_LOW:
                statusBarHeight = LOW_DPI_STATUS_BAR_HEIGHT;
                break;
            default:
                statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
        }

        return (float) statusBarHeight;
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
        if(field == null) {
            groupField.destroy();
        } else {
            field.destroy();
        }
        glsv.destroy();
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

        field.onTouch((event.getX() / fDisplayHeight) * 2 - fDispHalfRelativeWidth, -(event.getY() / fDisplayHeight) * 2 + 1f);

        return false;
    }
}

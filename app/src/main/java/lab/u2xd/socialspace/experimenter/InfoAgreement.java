package lab.u2xd.socialspace.experimenter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import lab.u2xd.socialspace.R;

public class InfoAgreement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_agreement);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info_agreement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Confirm_Click(View view) {
        Intent intentResult = getIntent();
        CheckBox checkBox = (CheckBox) findViewById(R.id.info_agreement_checkbox_1);
        if(checkBox.isChecked()) {
            int agreement = 100;
            if(((CheckBox) findViewById(R.id.info_agreement_checkbox_2)).isChecked())
                agreement += 10;
            if(((CheckBox) findViewById(R.id.info_agreement_checkbox_3)).isChecked())
                agreement += 1;

            intentResult.putExtra("AgreementType", agreement);
            setResult(RESULT_OK, intentResult);
            finish();
        } else {
            Toast.makeText(this, "필수적 정보 수집 및 이용에 동의하여 주십시오", Toast.LENGTH_LONG).show();
        }
    }
}

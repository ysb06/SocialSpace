package lab.u2xd.socialspace.experimenter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;

import lab.u2xd.socialspace.R;

public class BasicInfo extends AppCompatActivity implements View.OnClickListener {

    private EditText fieldName;
    private EditText fieldAge;
    private EditText fieldMajor;
    private RadioButton radioMan;
    private RadioButton radioWoman;
    private EditText fieldNumber;
    private EditText fieldEmail;

    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info_ui);

        fieldName = (EditText) findViewById(R.id.basicinfo_field_name);
        fieldAge = (EditText) findViewById(R.id.basicinfo_field_age);
        fieldMajor = (EditText) findViewById(R.id.basicinfo_field_major);
        radioMan = (RadioButton) findViewById(R.id.basicinfo_radio_man);
        radioMan.setOnClickListener(this);
        radioWoman = (RadioButton) findViewById(R.id.basicinfo_radio_woman);
        radioWoman.setOnClickListener(this);
        fieldNumber = (EditText) findViewById(R.id.basicinfo_field_number);
        fieldEmail = (EditText) findViewById(R.id.basicinfo_field_email);

        btnFinish = (Button) findViewById(R.id.basicinfo_button_finish);
        btnFinish.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basic_info_ui, menu);
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

    //버튼 클릭 이벤트
    @Override
    public void onClick(View v) {
        if(v.equals(btnFinish)) {
            if (fieldName.getText().length() <= 0)
                Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
            else if (fieldAge.getText().length() <= 0)
                Toast.makeText(this, "나이를 입력하세요", Toast.LENGTH_SHORT).show();
            else if (fieldMajor.getText().length() <= 0)
                Toast.makeText(this, "학과를 입력하세요", Toast.LENGTH_SHORT).show();
            else if (fieldNumber.getText().length() <= 0)
                Toast.makeText(this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
            else {
                Intent intentResult = getIntent();
                intentResult.putExtra("expName", fieldName.getText().toString());
                intentResult.putExtra("expAge", Integer.parseInt(fieldAge.getText().toString()));
                intentResult.putExtra("expMajor", fieldMajor.getText().toString());
                if(radioMan.isChecked()) {
                    intentResult.putExtra("expGender", "Man");
                } else {
                    intentResult.putExtra("expGender", "Woman");
                }
                intentResult.putExtra("expNumber", fieldNumber.getText().toString());
                intentResult.putExtra("expEmail", fieldEmail.getText().toString());
                setResult(RESULT_OK, intentResult);
                finish();
            }
        } else if(v.equals(radioMan)) {
            radioWoman.setChecked(false);
        } else if(v.equals(radioWoman)) {
            radioMan.setChecked(false);
        }
    }
}

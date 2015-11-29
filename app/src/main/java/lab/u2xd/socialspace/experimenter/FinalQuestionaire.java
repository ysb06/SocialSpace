package lab.u2xd.socialspace.experimenter;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.annotation.DimenRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import lab.u2xd.socialspace.R;

public class FinalQuestionaire extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private RelativeLayout layoutQuestionaire;
    private ArrayList<SeekBar> listAnswers;
    private HashMap<SeekBar, TextView> listReferPosition;
    private HashMap<SeekBar, TextView> listLeftText;
    private HashMap<SeekBar, TextView> listRightText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_questionaire);

        Resources res = getResources();
        layoutQuestionaire = (RelativeLayout) findViewById(R.id.final_questionaire_layout);
        listAnswers = new ArrayList<>();
        listLeftText = new HashMap<>();
        listRightText = new HashMap<>();
        listReferPosition = new HashMap<>();
        addQuestion(R.string.final_questionaire_text_sel1, R.string.final_questionaire_text_sel2);
        addQuestion(R.string.final_questionaire_text_sel1, R.string.final_questionaire_text_sel3);
        addQuestion(R.string.final_questionaire_text_sel1, R.string.final_questionaire_text_sel4);
        addQuestion(R.string.final_questionaire_text_sel1, R.string.final_questionaire_text_sel5);
        addQuestion(R.string.final_questionaire_text_sel1, R.string.final_questionaire_text_sel6);
        addQuestion(R.string.final_questionaire_text_sel2, R.string.final_questionaire_text_sel3);
        addQuestion(R.string.final_questionaire_text_sel2, R.string.final_questionaire_text_sel4);
        addQuestion(R.string.final_questionaire_text_sel2, R.string.final_questionaire_text_sel5);
        addQuestion(R.string.final_questionaire_text_sel2, R.string.final_questionaire_text_sel6);
        addQuestion(R.string.final_questionaire_text_sel3, R.string.final_questionaire_text_sel4);
        addQuestion(R.string.final_questionaire_text_sel3, R.string.final_questionaire_text_sel5);
        addQuestion(R.string.final_questionaire_text_sel3, R.string.final_questionaire_text_sel6);
        addQuestion(R.string.final_questionaire_text_sel4, R.string.final_questionaire_text_sel5);
        addQuestion(R.string.final_questionaire_text_sel4, R.string.final_questionaire_text_sel6);
        addQuestion(R.string.final_questionaire_text_sel5, R.string.final_questionaire_text_sel6);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_final_questionaire, menu);
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

    /** 쌍대 비교 문항을 UI를 추가
     *
     * @param string_id1 기준 문항
     * @param string_id2 비교할 문항
     */
    private void addQuestion(int string_id1, int string_id2) {
        Resources res = getResources();

        //레이아웃 설정
        LinearLayout layoutSub = new LinearLayout(this);
        layoutSub.setOrientation(LinearLayout.HORIZONTAL);
        layoutSub.setId(2 * listReferPosition.size() + 1);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if(listReferPosition.size() <= 0) {
            layoutParams.addRule(RelativeLayout.BELOW, R.id.final_questionaire_first_hint);
        } else {
            layoutParams.addRule(RelativeLayout.BELOW, listReferPosition.get(listAnswers.get(listAnswers.size() - 1)).getId());
        }
        layoutSub.setLayoutParams(layoutParams);

        //레이아웃 내부 요소 설정
        TextView textLeft = new TextView(this);
        SeekBar bar = new SeekBar(this);
        TextView textRight = new TextView(this);

        listAnswers.add(bar);
        bar.setMax(200);
        bar.setProgress(100);
        LinearLayout.LayoutParams barParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        barParam.weight = 1;
        bar.setLayoutParams(barParam);
        bar.setOnSeekBarChangeListener(this);
        bar.setKeyProgressIncrement(1);

        textLeft.setText(res.getString(string_id1));
        textLeft.setTextSize(res.getDimension(R.dimen.questionaire_text_size));
        textLeft.setWidth(res.getDimensionPixelSize(R.dimen.questionaire_text_width));
        listLeftText.put(bar, textLeft);

        textRight.setText(res.getString(string_id2));
        textRight.setTextSize(res.getDimension(R.dimen.questionaire_text_size));
        textRight.setWidth(res.getDimensionPixelSize(R.dimen.questionaire_text_width));
        listRightText.put(bar, textRight);

        layoutSub.addView(textLeft);
        layoutSub.addView(bar);
        layoutSub.addView(textRight);

        //실험자가 선택한 답 표시 부분
        TextView numHint = new TextView(this);
        numHint.setId(2 * listReferPosition.size() + 2);
        numHint.setText("0");
        numHint.setTextSize(res.getDimension(R.dimen.questionaire_text_small_size));
        numHint.setPadding(0, 0, 0, res.getDimensionPixelSize(R.dimen.questionaire_text_padding));

        RelativeLayout.LayoutParams numHintParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        numHintParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        numHintParams.addRule(RelativeLayout.BELOW, layoutSub.getId());
        numHint.setLayoutParams(numHintParams);
        listReferPosition.put(bar, numHint);

        Log.e("Final Questionaire", "Added -> " + layoutSub.getId());
        layoutQuestionaire.addView(layoutSub);
        Log.e("Final Questionaire", "Added -> " + numHint.getId());
        layoutQuestionaire.addView(numHint);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TextView text = listReferPosition.get(seekBar);
        float fWeight = (100 - (float) progress) / 10;
        if(progress < 100) {
            text.setText(listLeftText.get(seekBar).getText() + " : " + fWeight);
        } else if(progress == 100) {
            text.setText("" + fWeight);
        } else if(progress > 100) {
            text.setText(listRightText.get(seekBar).getText() + " : " + (-fWeight));
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

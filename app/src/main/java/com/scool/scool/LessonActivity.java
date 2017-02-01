package com.scool.scool;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LessonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
    }

    public void SendSum(View v){
        EditText txt = (EditText)findViewById(R.id.Content);

        TextView new_txt = new TextView(this);
        new_txt.setText(txt.getText());
        new_txt.setTextSize(30);
        new_txt.setBackgroundColor(Color.GREEN);

        LinearLayout ly = (LinearLayout)findViewById(R.id.SumTxt);
        LinearLayout empty = new LinearLayout(this);
        ly.addView(new_txt);
    }
}

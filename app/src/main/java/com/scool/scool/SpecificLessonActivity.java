package com.scool.scool;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;

public class SpecificLessonActivity extends AppCompatActivity {
    private Toast prev_toast = null;
    public static final int POST_PIXEL_SIZE = 250;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        SpecificLesson lesson = extras.getParcelable("lesson object"); //The SpecificLessonObject to display to the screen
        setTitle(lesson.class_name + " with " + lesson.teacher + " at " + lesson.date);
        Collections.sort(lesson.posts, new Comparator<Post>()
        {
            public int compare(Post p1, Post p2)
            {
                return p1.serial_number - p2.serial_number;
            }
        });
        add_to_design(lesson);


    }

    public void add_to_design(SpecificLesson lesson){
        LinearLayout posts_layout = (LinearLayout)findViewById(R.id.postsLayout);
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = this.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        for(final Post post: lesson.posts){
            LinearLayout curr_layout = new LinearLayout(this);
            curr_layout.setBackgroundResource(backgroundResource);
            curr_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, POST_PIXEL_SIZE));
            final SpecificLessonActivity this_specific_activity = this;
            curr_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    my_toast(post._text);
                }
            });

            TextView txt = new TextView(this);
            txt.setTextColor(Color.BLUE);
            txt.setText(post._text);

            curr_layout.addView(txt);

            posts_layout.addView(curr_layout);
        }
        typedArray.recycle();
    }
    private void my_toast(String message){
        if(prev_toast != null){
            prev_toast.cancel();
        }
        prev_toast = Toast.makeText(this.getApplicationContext(), message,
                Toast.LENGTH_SHORT);
        prev_toast.show();
    }

    private  void my_toast(String message, int length){
        if(prev_toast != null){
            prev_toast.cancel();
        }
        prev_toast = Toast.makeText(this.getApplicationContext(), message,
                length);
        prev_toast.show();
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}


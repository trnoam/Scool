package com.scool.scool;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class LoginActivity extends AppCompatActivity {

    private int var;
    private Toast prev_toast = null;
    DatabaseReference myRef = null;
    LinearLayout main_layout = null;
    public static int PIXEL_PER_MINUTE = 3;
    Vector<DataSnapshot> all_classes_today = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        final String date = df.format(Calendar.getInstance().getTime());
        TextView date_txt = (TextView)findViewById(R.id.dateText);
        date_txt.setText(date);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        main_layout = (LinearLayout) findViewById(R.id.content_login);
        main_layout.setBackgroundColor(Color.WHITE);
        set_ui_components("trnoam");
    }

    private void set_ui_components(String username){
        all_classes_today = new Vector<>();
        myRef.child("users").child(username).child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot user_classes) {
                for(final DataSnapshot course: user_classes.getChildren()){
                    myRef.child("classes").child(course.getValue(String.class)).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot course_data) {
                                    if(course_data.exists()) {
                                        all_classes_today.add(course_data);
                                    }else{
                                        all_classes_today.add(null);
                                    }
                                    if (all_classes_today.size() == user_classes.getChildrenCount()) {
                                        start_ui_components(all_classes_today, 0);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    my_toast("Internet Error!!!");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                my_toast("Internet error!!!");
            }
        });
    }

    void start_ui_components(Vector<DataSnapshot> all_classes_data, int factor){
        List<SpecificLesson> ls = new ArrayList<SpecificLesson>();
        DateFormat df = new SimpleDateFormat("yyyy|MM|dd");
        DateFormat print_date = new SimpleDateFormat("dd.MM.yyyy");


        String date = "";
        String curr_txt = "";

        if (factor == 0)
             date = print_date.format(Calendar.getInstance().getTime());
        if (factor != 0){
            curr_txt = ((TextView) findViewById(R.id.dateText)).getText().toString();
            int day = Integer.parseInt(curr_txt.substring(0, 2)) + factor;
            curr_txt = Integer.toString(day) + curr_txt.substring(2);
            date = curr_txt;
        }

        ((TextView)findViewById(R.id.dateText)).setText(date);

        final String date_fnl = date.substring(6) + "|" + date.substring(3,5) + "|" + date.substring(0,2);

        for(DataSnapshot course: all_classes_data){
            if(course == null){
                continue;
            }
            if(!course.child("times").child(date_fnl).exists()){
                continue;
            }
            for(DataSnapshot specific_lesson: course.child("times").child(date_fnl).getChildren()){
                SpecificLesson curr = new SpecificLesson(course, specific_lesson, date);
                ls.add(curr);
            }
        }
        Collections.sort(ls, new Comparator<SpecificLesson>()
        {
            public int compare(SpecificLesson l1, SpecificLesson l2)
            {
                return l1.start_time.compareTo(l2.start_time);
            }
        });
        add_to_design(ls);

    }
    private void add_to_design(List<SpecificLesson> ls){
        SpecificLesson prev = null;
        final LinearLayout classes_layout = (LinearLayout)findViewById(R.id.classesLayout);

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = this.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);



        for(final SpecificLesson lesson: ls){
            final LinearLayout class_new = new LinearLayout(this);
            class_new.setBackgroundResource(backgroundResource);
            class_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    lesson.lesson_diff() * PIXEL_PER_MINUTE));

            final LoginActivity this_login_activity = this; //save this activity

            class_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(this_login_activity, SpecificLessonActivity.class);
                    //intent.putExtra("lesson object", lesson);
                    startActivity(intent);
                }
            });

            if(prev != null){
                LinearLayout space_between_classes = new LinearLayout(this);
                space_between_classes.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        prev.lesson_diff(lesson) * PIXEL_PER_MINUTE));
                space_between_classes.setBackgroundColor(Color.GRAY);
                classes_layout.addView(space_between_classes);
            }

            TextView txt = new TextView(this);
            txt.setText(lesson.class_name);
            txt.setTextSize(20);
            txt.setTextColor(Color.WHITE);

            String start = lesson.start_time + lesson.end_time;
            start = start.substring(0,2) + ":" + start.substring(3, 5) + " - " + start.substring(5, 7) + ":" + start.substring(8, 10) + "     ";


            TextView txt_hours = new TextView(this);
            txt_hours.setText(start);
            txt_hours.setTextSize(20);
            txt_hours.setTextColor(Color.WHITE);

            class_new.setBackgroundColor(Color.parseColor("#9FA8DA"));
            class_new.setPadding(20, 15, 0, 0);
            class_new.addView(txt_hours);
            class_new.addView(txt);

            findViewById(R.id.scrollView).setOnTouchListener(new OnSwipeTouchListener(LoginActivity.this){
                public void onSwipeRight() {
                    classes_layout.removeAllViews();
                    start_ui_components(all_classes_today, -1);
                }
                public void onSwipeLeft() {
                    classes_layout.removeAllViews();
                    start_ui_components(all_classes_today, 1);
                }
            });

            classes_layout.setPadding(0, 0, 0, 0);
            classes_layout.addView(class_new);

            prev = lesson;
        }
        typedArray.recycle();
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}

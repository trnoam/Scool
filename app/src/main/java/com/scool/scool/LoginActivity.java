package com.scool.scool;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
        set_ui_components("trnoam");
    }

    private void set_ui_components(String username){
        final Vector<DataSnapshot> all_classes_today = new Vector<>();
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
                                        start_ui_components(all_classes_today);
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

    void start_ui_components(Vector<DataSnapshot> all_classes_data){
        List<SpecificLesson> ls = new ArrayList<SpecificLesson>();
        DateFormat df = new SimpleDateFormat("yyyy|MM|dd");
        final String date = df.format(Calendar.getInstance().getTime());

        for(DataSnapshot course: all_classes_data){
            if(course == null){
                continue;
            }
            if(!course.child("times").child(date).exists()){
                continue;
            }
            for(DataSnapshot specific_lesson: course.child("times").child(date).getChildren()){
                SpecificLesson curr = new SpecificLesson();
                curr.class_name = course.child("name").getValue(String.class);
                curr.date = date;
                curr.admin = course.child("admin").getValue(String.class);
                curr.teacher = course.child("teacher").getValue(String.class);
                curr.start_time = specific_lesson.getKey();
                curr.end_time = specific_lesson.child("finish time").getValue(String.class);
                curr.class_id = course.getKey();
                curr.posts = specific_lesson.child("posts");
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
        for(SpecificLesson lesson: ls){
            add_to_design(lesson);
        }

    }
    private void add_to_design(SpecificLesson lesson){

        LinearLayout classes_layout = (LinearLayout)findViewById(R.id.classesLayout);

        LinearLayout class_new = new LinearLayout(this);
        class_new.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
        class_new.setBackgroundColor(Color.BLUE);

        TextView txt = new TextView(this);
        txt.setText(lesson.class_name);
        txt.setTextColor(Color.WHITE);

        class_new.addView(txt);

        classes_layout.addView(class_new);
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

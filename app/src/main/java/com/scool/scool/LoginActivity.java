package com.scool.scool;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.marytts.android.link.MaryLink;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import static java.security.AccessController.getContext;

public class LoginActivity extends AppCompatActivity {

    private final int ID_LENGTH = 8;
    private int var;
    private Toast prev_toast = null;
    DatabaseReference myRef = null;
    LinearLayout main_layout = null;
    public static int PIXEL_PER_MINUTE = 3;
    Vector<DataSnapshot> all_classes_today = null;
    public List<SpecificLesson> ls = null;
    Calendar calendar = null;
    String curr_date, username;
    Random rand = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final String date = get_date();

        init_calendar_and_date();
        TextView date_txt = (TextView)findViewById(R.id.dateText);
        date_txt.setText(date);

        username = "trnoam"; //TODO: change to dynamic username.

        Button button = (Button) findViewById(R.id.add_lesson);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ls == null){
                    return;
                }
                final ProgressDialog progressDialog= ProgressDialog.show(LoginActivity.this, "", "Loading. Please wait...", true);
                myRef.child("users").child(username).child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot user_classes) {
                        if(user_classes.getChildrenCount() == 0){
                            return;
                        }
                        final String[] names = new String[(int)user_classes.getChildrenCount()];
                        int count = 0;
                        final int[] count2 = new int[1];
                        count2[0] = 0;
                        for(DataSnapshot course: user_classes.getChildren()){
                            final int i = count;
                            count++;
                            myRef.child("classes").child(course.getValue(String.class)).child("name").
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot course_name) {
                                            names[i] = course_name.getValue(String.class);
                                            count2[0]++;
                                            if(count2[0] >= user_classes.getChildrenCount()){
                                                final String[] ids = new String[(int)user_classes.getChildrenCount()];
                                                progressDialog.dismiss();
                                                final Dialog dialog = new Dialog(LoginActivity.this);
                                                dialog.setContentView(R.layout.custom);
                                                dialog.setTitle("Add lesson");
                                                ArrayList<String> spinnerArrayCourse = new ArrayList<String>();
                                                int i1 = 0;
                                                for (DataSnapshot course: user_classes.getChildren()){
                                                    spinnerArrayCourse.add(names[i1] + " - " + course.getValue(String.class));
                                                    ids[i1] = course.getValue(String.class);
                                                    i1++;
                                                }
                                                final Spinner spinner = (Spinner)dialog.findViewById(R.id.spinner_courses);
                                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(LoginActivity.this,
                                                        android.R.layout.simple_spinner_dropdown_item, spinnerArrayCourse);
                                                spinner.setAdapter(spinnerArrayAdapter);

                                                final Spinner spinner_yyyy = (Spinner)dialog.findViewById(R.id.yyyy);
                                                ArrayAdapter<String> spinnerArrayAdapter_yyyy = new ArrayAdapter<String>(LoginActivity.this,
                                                        android.R.layout.simple_spinner_dropdown_item, getSpinnerValue(1990, 2020));
                                                spinner_yyyy.setAdapter(spinnerArrayAdapter_yyyy);

                                                final Spinner spinner_dd = (Spinner)dialog.findViewById(R.id.dd);
                                                ArrayAdapter<String> spinnerArrayAdapter_dd = new ArrayAdapter<String>(LoginActivity.this,
                                                        android.R.layout.simple_spinner_dropdown_item, getSpinnerValue(1, 32));
                                                spinner_dd.setAdapter(spinnerArrayAdapter_dd);

                                                final Spinner spinner_mm = (Spinner)dialog.findViewById(R.id.mm);
                                                ArrayAdapter<String> spinnerArrayAdapter_mm = new ArrayAdapter<String>(LoginActivity.this,
                                                        android.R.layout.simple_spinner_dropdown_item, getSpinnerValue(1, 12));
                                                spinner_mm.setAdapter(spinnerArrayAdapter_mm);

                                                final Spinner spinner_f_hh = (Spinner)dialog.findViewById(R.id.from_hh);
                                                ArrayAdapter<String> spinnerArrayAdapter_f_hh = new ArrayAdapter<String>(LoginActivity.this,
                                                        android.R.layout.simple_spinner_dropdown_item, getSpinnerValue(0, 23));
                                                spinner_f_hh.setAdapter(spinnerArrayAdapter_f_hh);

                                                final Spinner spinner_f_mm = (Spinner)dialog.findViewById(R.id.from_mm);
                                                ArrayAdapter<String> spinnerArrayAdapter_f_mm = new ArrayAdapter<String>(LoginActivity.this,
                                                        android.R.layout.simple_spinner_dropdown_item, getSpinnerValue(0, 59));
                                                spinner_f_mm.setAdapter(spinnerArrayAdapter_f_mm);

                                                final Spinner spinner_t_hh = (Spinner)dialog.findViewById(R.id.to_hh);
                                                ArrayAdapter<String> spinnerArrayAdapter_t_hh = new ArrayAdapter<String>(LoginActivity.this,
                                                        android.R.layout.simple_spinner_dropdown_item, getSpinnerValue(0, 23));
                                                spinner_t_hh.setAdapter(spinnerArrayAdapter_t_hh);

                                                final Spinner spinner_t_mm = (Spinner)dialog.findViewById(R.id.to_mm);
                                                ArrayAdapter<String> spinnerArrayAdapter_t_mm = new ArrayAdapter<String>(LoginActivity.this,
                                                        android.R.layout.simple_spinner_dropdown_item, getSpinnerValue(0, 59));
                                                spinner_t_mm.setAdapter(spinnerArrayAdapter_t_mm);

                                                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogButtonCancelCreate);
                                                // if button is clicked, close the custom dialog
                                                dialogCancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String lesson_date = (String)spinner_yyyy.getSelectedItem() + "|" +
                                                                (String)spinner_mm.getSelectedItem() + "|" +
                                                                (String)spinner_dd.getSelectedItem();
                                                        String start_time = (String)spinner_f_hh.getSelectedItem() + "|" +
                                                                (String)spinner_f_mm.getSelectedItem();
                                                        String finish_time = (String)spinner_t_hh.getSelectedItem() + "|" +
                                                                (String)spinner_t_mm.getSelectedItem();
                                                        my_toast(lesson_date + "-" + start_time + "-" + finish_time);
                                                        myRef.child("classes").child(ids[spinner.getSelectedItemPosition()]).
                                                                child("times").child(lesson_date).child(start_time).
                                                                child("finish time").setValue(finish_time).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                my_toast("lesson added");
                                                                dialog.dismiss();
                                                                set_ui_components(username);
                                                            }
                                                        });
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            my_toast("Internet error!!!");
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
        });

        Button button_join_course = (Button) findViewById(R.id.join_course);

        button_join_course.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ls == null){
                    return;
                }
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.join);
                dialog.setTitle("Join Course:");
                final EditText course_id_edit_text = (EditText)dialog.findViewById(R.id.course_id_join);
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogButtonCancelCreate);
                // if button is clicked, close the custom dialog
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        my_toast("Please wait...");
                        if(course_id_edit_text.getText() != null && !course_id_edit_text.getText().toString().isEmpty()) {
                            myRef.child("classes").child(String.valueOf(course_id_edit_text.getText())).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot course_object) {
                                            if(course_object.exists()){
                                                myRef.child("users").child(username).child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot user_classes_list) {
                                                        myRef.child("users").child(username).child("classes").child(
                                                                Integer.toString((int)user_classes_list.getChildrenCount() + 1)).
                                                                setValue(String.valueOf(course_id_edit_text.getText())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                my_toast("Succesfully joined course " + course_object.child("name").getValue(String.class));
                                                                dialog.dismiss();
                                                                LoginActivity.this.set_ui_components(LoginActivity.this.username);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        my_toast("Error: username doesn't exist");
                                                    }
                                                });
                                            }else{
                                                my_toast("No course with id " + String.valueOf(course_id_edit_text.getText()) + " exists");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            my_toast("Internet error!!!");
                                        }
                                    });
                        }else{
                            my_toast("Nothing inserted");
                        }
                    }
                });
                dialog.show();
            }
        });

        Button button_create_course = (Button) findViewById(R.id.create_course);

        button_create_course.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ls == null){
                    return;
                }
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.create);
                dialog.setTitle("Create Course:");
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOKCreate);
                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogButtonCancelCreate);
                final EditText teacher_name_edit_text = (EditText)dialog.findViewById(R.id.teacher_name);
                final EditText admin_username_edit_text = (EditText)dialog.findViewById(R.id.admin_username);
                final EditText course_name_edit_text = (EditText)dialog.findViewById(R.id.course_name);

                // if button is clicked, close the custom dialog
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String id = get_random_id();
                        Map<String, Object> course_object = new HashMap<>();
                        if(teacher_name_edit_text.getText().toString().isEmpty() ||
                                admin_username_edit_text.getText().toString().isEmpty()||
                                course_name_edit_text.getText().toString().isEmpty()){
                            my_toast("The fields can't be empty");
                        }else{
                            course_object.put("teacher", teacher_name_edit_text.getText().toString());
                            course_object.put("admin", admin_username_edit_text.getText().toString());
                            course_object.put("name", course_name_edit_text.getText().toString());
                            myRef.child("classes").child(id).setValue(course_object);
                            myRef.child("users").child(username).child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot user_classes) {
                                    myRef.child("users").child(username).child("classes").
                                            child(Integer.toString((int)user_classes.getChildrenCount() + 1)).setValue(id).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            my_toast("course " + course_name_edit_text.getText().toString() +
                                                    " with id " + id + " successfully created", Toast.LENGTH_LONG);
                                            dialog.dismiss();
                                            set_ui_components(username);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    my_toast("Internet error!!!");
                                }
                            });

                        }
                    }
                });
                dialog.show();
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        main_layout = (LinearLayout) findViewById(R.id.content_login);
        main_layout.setBackgroundColor(Color.WHITE);
        set_ui_components(username);
    }

    public void set_ui_components(String username){
        final LinearLayout classes_layout = (LinearLayout)findViewById(R.id.classesLayout);
        classes_layout.removeAllViews();
        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "", "Loading. Please wait...", true);
        classes_layout.setOnTouchListener(new OnSwipeTouchListener(LoginActivity.this) {
            public void onSwipeRight() {
                reduce_date_by_1();
            }
            public void onSwipeLeft() {
                advance_date_by_1();
            }
        });
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
                                        start_ui_components(all_classes_today);
                                        dialog.dismiss();
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
        ls = new ArrayList<SpecificLesson>();

        ((TextView)findViewById(R.id.dateText)).setText(curr_date);

        final String date_fnl = curr_date.substring(6) + "|" + curr_date.substring(3,5) + "|" + curr_date.substring(0,2);

        for(DataSnapshot course: all_classes_data){
            if(course == null){
                continue;
            }
            if(!course.child("times").child(date_fnl).exists()){
                continue;
            }
            for(DataSnapshot specific_lesson: course.child("times").child(date_fnl).getChildren()){
                SpecificLesson curr = new SpecificLesson(course, specific_lesson, curr_date);
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
                    Intent intent = new Intent(this_login_activity, LessonActivity.class);
                    intent.putExtra("lesson object", lesson);
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
                    start_ui_components(all_classes_today);
                }
                public void onSwipeLeft() {
                    classes_layout.removeAllViews();
                    start_ui_components(all_classes_today);
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
    private void add_lesson(String id, String date, String start_h, String end_h, final Dialog dismiss_when_done) {
        myRef.child("classes").child(id).child("times").child(date).child(start_h).child("finish time").setValue(end_h, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                dismiss_when_done.dismiss();
            }
        });
    }
    private void init_calendar_and_date(){
        curr_date = get_date();  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(curr_date));
        } catch (ParseException e) {
            my_toast("Sorry, error");
        }
    }
    private  void advance_date_by_1(){
        calendar.add(Calendar.DATE, 1);  // number of days to add
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        curr_date = sdf.format(calendar.getTime());  // dt is now the new date
        set_ui_components(username);
    }
    private void reduce_date_by_1(){
        calendar.add(Calendar.DATE, -1);  // number of days to add
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        curr_date = sdf.format(calendar.getTime());  // dt is now the new date
        set_ui_components(username);
    }
    private String get_date(){
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        return df.format(date);
    }
    private String get_random_id(){
        String id = "";
        for(int i = 0; i < ID_LENGTH; i++){
            id += get_random_char();
        }
        return id;
    }
    private char get_random_char(){
        if(rand == null){
            rand = new Random();
        }
        double p = ('9' - '0' + 1) / ('z' - 'a' + 1.0);
        if(Math.random() < p){
            return (char)(rand.nextInt('9' - '0' + 1) + '0');
        }else{
            return (char)(rand.nextInt('Z' - 'A' + 1) + 'A');
        }
    }
    private ArrayList<String> getSpinnerValue(int from, int to){
        if(from <= 1000){
            ArrayList<String> spinnerArray = new ArrayList<String>();
            for(int i = from; i <= to; i++){
                if(i < 10){
                    spinnerArray.add("0" + i);
                }else{
                    spinnerArray.add(i + "");
                }
            }
            return spinnerArray;
        }else{
            ArrayList<String> spinnerArray = new ArrayList<String>();
            for(int i = from; i <= to; i++){
                spinnerArray.add(i + "");
            }
            return spinnerArray;
        }
    }
}

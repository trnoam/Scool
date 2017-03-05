package com.scool.scool;

import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.marytts.android.link.MaryLink;

import java.util.Locale;
import java.util.Queue;

public class LessonActivity extends AppCompatActivity {

    DatabaseReference myRef = null;
    DatabaseReference posts_ref = null;
    SpecificLesson lesson = null;
    TextToSpeech t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        //MaryLink.load(this);

         t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
                else{
                    Toast t = new Toast(LessonActivity.this);
                    t.makeText(LessonActivity.this, "akakak", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });

        lesson = getIntent().getExtras().getParcelable("lesson object");

        lesson.date = lesson.date.replace('.', ',');
        String[] times = (lesson.date).split(",");
        lesson.date = times[2] + "|" + times[1] + "|" + times[0];

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        posts_ref =  myRef.child("classes").child(lesson.class_id).child("times").
                child(lesson.date).child(lesson.start_time).child("posts");
        posts_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot posts) {
                LinearLayout del = (LinearLayout)findViewById(R.id.SumTxt);
                del.removeAllViews();
                for (DataSnapshot post : posts.getChildren()){
                    add_post_by_text(post.child("text").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void SendSum(View v){
        final EditText txt = (EditText)findViewById(R.id.Content);
        posts_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot posts) {
                int size = (int)posts.getChildrenCount() + 1;
                String text = txt.getText().toString();
                posts_ref.child(Integer.toString(size)).child("text").setValue(text);
                txt.setText("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(LessonActivity.this, "Internet error!!!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void add_post_by_text(String txt){

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            t1.speak(txt, TextToSpeech.QUEUE_FLUSH, null, null);
        }*/
        //MaryLink.getInstance().startTTS(txt);

        TextView new_txt = new TextView(this);
        new_txt.setText(txt);
        new_txt.setTextSize(30);
        new_txt.setBackgroundColor(Color.GREEN);

        LinearLayout ly = (LinearLayout)findViewById(R.id.SumTxt);
        LinearLayout empty = new LinearLayout(this);

        empty.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10));

        ly.addView(new_txt);
        ly.addView(empty);

        final ScrollView scroll = (ScrollView)findViewById(R.id.scrollView);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}

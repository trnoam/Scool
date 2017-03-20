package com.scool.scool;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.marytts.android.link.MaryLink;

import java.util.Locale;
import java.util.Queue;
import java.util.Vector;

public class LessonActivity extends AppCompatActivity {

    DatabaseReference myRef = null;
    DatabaseReference posts_ref = null;
    FirebaseStorage firebaseStorage = null;
    public static StorageReference storageRef = null;
    SpecificLesson lesson = null;
    TextToSpeech t1;
    public static int count;
    public static Toast prev_toast = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();

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
                int i = 1;
                for (DataSnapshot post : posts.getChildren()){
                    if(i == posts.getChildrenCount()){
                        add_post_by_text(post.child("text").getValue(String.class), true);
                    }else{
                        add_post_by_text(post.child("text").getValue(String.class), false);
                    }
                    i++;
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

    public void add_post_by_text(String txt, Boolean is_sound){

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            t1.speak(txt, TextToSpeech.QUEUE_FLUSH, null, null);
        }*/
        //MaryLink.getInstance().startTTS(txt);
        if(is_sound) {
            play_string(txt);
        }

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

    public void play_string(final String str1){
        final String str = str1.toLowerCase();
        final String[] splited = str.split(" ");
        LessonActivity.count = 0;
        int index = 0;
        final byte[][] sounds = new byte[splited.length][];
        for(String s : splited){
            final int i = index;
            StorageReference islandRef = storageRef.child("words/" + s + ".wav");
            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    LessonActivity.count++;
                    sounds[i] = bytes;
                    if (LessonActivity.count == splited.length){
                        LessonActivity.this.play(sounds, str);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    LessonActivity.count++;
                    sounds[i] = null;
                    if (LessonActivity.count == splited.length){
                        LessonActivity.this.play(sounds, str);
                    }
                }
            });
            index++;
        }
    }
    public void play(byte[][] sounds, String str){
        int min_buffer_size = 100000;
        for(byte[] sound: sounds){
            if (sound.length > min_buffer_size){
                min_buffer_size = sound.length;
            }
        }
        my_toast("Playing " + str);
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, min_buffer_size, AudioTrack.MODE_STREAM);
        for(byte[] sound: sounds){
            if (sound == null){
                continue;
            }
            audioTrack.write(sound, 0, sound.length );
            audioTrack.play();
        }
    }
    public void my_toast(String message){
        if(prev_toast != null){
            prev_toast.cancel();
        }
        prev_toast = Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT);
        prev_toast.show();
    }

    public void my_toast(String message, int length){
        if(prev_toast != null){
            prev_toast.cancel();
        }
        prev_toast = Toast.makeText(this.getApplicationContext(), message,
                length);
        prev_toast.show();
    }
}

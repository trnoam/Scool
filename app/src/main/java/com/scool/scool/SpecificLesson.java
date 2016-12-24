package com.scool.scool;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 12/18/2016.
 */

public class SpecificLesson implements Parcelable{
    public String class_name;
    public String date;
    public String admin;
    public String teacher;
    public String start_time;
    public String end_time;
    public String class_id;
    public List<Post> posts;
    SpecificLesson(){

    }

    public int lesson_diff(){
        int hours = Integer.parseInt(end_time.substring(0,2)) - Integer.parseInt(start_time.substring(0,2));
        int minutes = Integer.parseInt(end_time.substring(3,5)) - Integer.parseInt(start_time.substring(3,5));
        return hours * 60 + minutes;
    }
    // Constructor
    public SpecificLesson(DataSnapshot course, DataSnapshot specific_lesson, String date){
        class_name = course.child("name").getValue(String.class);
        this.date = date;
        admin = course.child("admin").getValue(String.class);
        teacher = course.child("teacher").getValue(String.class);
        start_time = specific_lesson.getKey();
        end_time = specific_lesson.child("finish time").getValue(String.class);
        class_id = course.getKey();
        posts = new ArrayList<>();
        for(DataSnapshot post_data: specific_lesson.child("posts").getChildren()){
            posts.add(new Post(post_data));
        }
    }

    public SpecificLesson(Parcel in){
        class_name = in.readString();
        date = in.readString();
        admin = in.readString();
        teacher = in.readString();
        start_time = in.readString();
        end_time = in.readString();
        class_id = in.readString();
        int amount_posts = in.readInt();
        posts = new ArrayList<>();
        for(int i = 0; i < amount_posts; i++){
            posts.add(new Post(in));
        }
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(class_name);
        dest.writeString(date);
        dest.writeString(admin);
        dest.writeString(teacher);
        dest.writeString(start_time);
        dest.writeString(end_time);
        dest.writeString(class_id);
        dest.writeInt(posts.size());
        for(Post post : posts){
            dest.writeParcelable(post, flags);
        }
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SpecificLesson createFromParcel(Parcel in) {
            return new SpecificLesson(in);
        }

        public SpecificLesson[] newArray(int size) {
            return new SpecificLesson[size];
        }
    };
}

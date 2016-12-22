package com.scool.scool;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by User on 12/18/2016.
 */

public class SpecificLesson {
    public String class_name;
    public String date;
    public String admin;
    public String teacher;
    public String start_time;
    public String end_time;
    public String class_id;
    public DataSnapshot posts;
    SpecificLesson(){

    }

    public int lesson_diff(){
        int hours = Integer.parseInt(end_time.substring(0,2)) - Integer.parseInt(start_time.substring(0,2));
        int minutes = Integer.parseInt(end_time.substring(3,5)) - Integer.parseInt(start_time.substring(3,5));
        return hours * 60 + minutes;
    }


}

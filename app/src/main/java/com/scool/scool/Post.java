package com.scool.scool;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 12/24/2016.
 */

public class Post implements Parcelable {
    public int serial_number;
    public String _text;
    public String _type;
    public List<ClassFile> _files;

    // Constructor
    public Post(){
    }
    public Post(DataSnapshot post_data){
        serial_number = Integer.parseInt(post_data.getKey());
        if(post_data.hasChild("text")){
            _text = post_data.child("text").getValue(String.class);
        }else{
            _text = "";
        }
        if(post_data.hasChild("type")){
            _type = post_data.child("type").getValue(String.class);
        }else{
            _type = "";
        }
        if(post_data.hasChild("files")){
            _files = new ArrayList<>();
            for(DataSnapshot file_data: post_data.child("files").getChildren()){
                _files.add(new ClassFile(file_data));
            }
        }else{
            _files = null;
        }
    }
    public Post(Parcel in){
        serial_number = in.readInt();
        _text = in.readString();
        _type = in.readString();
        int amount_files = in.readInt();
        _files = new ArrayList<>();
        for(int i = 0; i < amount_files; i++){
            _files.add(new ClassFile(in));
        }
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(serial_number);
        dest.writeString(_text);
        dest.writeString(_type);
        if(_files != null){
            dest.writeInt(_files.size());
            for (ClassFile file : _files) {
                dest.writeParcelable(file, flags);
            }
        }else{
            writeToParcel(dest, -1);
        }
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}

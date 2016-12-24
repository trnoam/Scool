package com.scool.scool;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by User on 12/24/2016.
 */

public class ClassFile implements Parcelable{
    public String _id;
    public int _size;
    public String _type;

    ClassFile(){

    }

    public ClassFile(Parcel in){
        _id = in.readString();
        _size = in.readInt();
        _type = in.readString();
    }

    public ClassFile(DataSnapshot class_file_data){
        if(class_file_data.hasChild("id")){
            _id = class_file_data.child("id").getValue(String.class);
        }else{
            _id = "";
        }
        if(class_file_data.hasChild("size")){
            _size = class_file_data.child("size").getValue(Integer.class);
        }else{
            _size = -1;
        }
        if(class_file_data.hasChild("type")){
            _type = class_file_data.child("type").getValue(String.class);
        }else{
            _type = "";
        }
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeInt(_size);
        dest.writeString(_type);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ClassFile createFromParcel(Parcel in) {
            return new ClassFile(in);
        }

        public ClassFile[] newArray(int size) {
            return new ClassFile[size];
        }
    };
}

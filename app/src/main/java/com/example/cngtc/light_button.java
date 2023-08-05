package com.example.cngtc;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class light_button {
    private String name;
    private String id;
    private int value;
    public String getName(){
        return this.name;
    }
    public String getId(){
        return this.id;
    }
    public int getValue(){
        return this.value;
    }

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("your-database");
    public void Push_to_firebase(){
        // push Tên của button lên
        myRef.child(this.id).child("Name").setValue(this.name);
        //push Status của button lên
        myRef.child(this.id).child("Status").setValue(this.value);
    }
    public void setButton(String id, String name, int value){
        this.name = name;
        this.id = id;
        this.value = value;

    }
};


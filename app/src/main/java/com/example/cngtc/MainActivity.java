package com.example.cngtc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private int buttonCount = 0;
    ImageButton add_button;
    LinearLayout container;
    TextView textView;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("your-database");

    public int newbtCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add_button = findViewById(R.id.add_button);
        container = findViewById(R.id.container);
        textView = findViewById(R.id.textView);
        loadButtonsFromFirebase();
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                builder.setView(dialogView);
                final EditText Buttonname = dialogView.findViewById(R.id.editTextButtonName);
                final EditText Buttonkey = dialogView.findViewById(R.id.editTextButtonKey);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String Name = Buttonname.getText().toString().trim();
                        String Id = Buttonkey.getText().toString().trim();
                        int value = 0;
                        //tạo 1 đối tượng Button
                        light_button lightButton = new light_button();
                        lightButton.setButton(Id,Name,value);
                        lightButton.Push_to_firebase();
                        //createButton(lightButton);

                    }

                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
    }
    private void loadButtonsFromFirebase() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String ID = snapshot.getKey();
                String NAME = snapshot.child("Name").getValue(String.class);
                int STATUS = snapshot.child("Status").getValue(Integer.class);
                light_button dtbBTN = new light_button();
                dtbBTN.setButton(ID,NAME,STATUS);
                createButton(dtbBTN);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void createButton(light_button lightButton) {
        String idButton = lightButton.getId();
        String nameButton = lightButton.getName();
        int statusButton = lightButton.getValue();
        Button button = new Button(this);
        button.setText(nameButton);
        // set layout cho newbutton
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Chiều dài (px hoặc dp)
                150  // Chiều rộng (px hoặc dp)
        );
        layoutParams.setMargins(100,50,100,50);

        layoutParams.gravity = Gravity.CENTER;
        button.setLayoutParams(layoutParams);

        // set màu cho button
        if(statusButton == 1){
            button.setBackground(new ColorDrawable(Color.YELLOW));
        }else{
            button.setBackground(new ColorDrawable(Color.WHITE));
        }
        // Thêm sự kiện click vào nút nhấn
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý sự kiện khi nút nhấn được nhấn

                myRef.child(idButton).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int currentValue = dataSnapshot.child("Status").getValue(Integer.class);
                        int updatedValue = (currentValue == 0) ? 1 : 0;
                        if(updatedValue == 1){
                            button.setBackground(new ColorDrawable(Color.YELLOW));
                        }else{
                            button.setBackground(new ColorDrawable(Color.WHITE));
                        }
                        myRef.child(idButton).child("Status").setValue(updatedValue);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý khi có lỗi xảy ra
                    }
                });
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // tạo 1 cái dialog để xác nhận xóa btn
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Bạn muốn xóa nút này ???");
                builder.setPositiveButton("Ừ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteButton(idButton);
                        // Xử lý khi người dùng nhấn nút "Đăng nhập" và lấy thông tin tên đăng nhập
                    }
                });
                builder.setNegativeButton("Chịu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //
                return true;
            }
        });
        // Thêm nút nhấn vào LinearLayout
        container.addView(button);
    }
    private void deleteButton(String id){
        myRef.child(id).removeValue();
        recreate();
    }
}
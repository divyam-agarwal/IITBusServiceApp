package com.example.avinashucheniya.bustracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogoutActivity extends AppCompatActivity {

    Button btnLogout;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        firebaseDatabase = FirebaseDatabase.getInstance();

        btnLogout = (Button)findViewById(R.id.B_logout);

        btnLogout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String user = getIntent().getExtras().getString("Driver_Username");
                        myRef = firebaseDatabase.getReference(user);

                        myRef.removeValue();

                        // Create object of SharedPreferences.
                        SharedPreferences local_database = PreferenceManager.getDefaultSharedPreferences(LogoutActivity.this);
                        //now get Editor
                        SharedPreferences.Editor editor = local_database.edit();
                        //put your value
                        editor.putBoolean("isLogin", false);
                        editor.putString("user",null);
                        //commits your edits
                        editor.commit();

                        Intent intent = new Intent(LogoutActivity.this,MapsActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }
}

package com.example.avinashucheniya.bustracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SuggestedBusStopActivity extends AppCompatActivity {

    Button btnSearch;
    EditText latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_bus_stop);

        btnSearch = findViewById(R.id.Btn_search);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);

        takeDestinationName();
    }

    public void takeDestinationName(){
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lati = latitude.getText().toString();
                String longi = longitude.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("latitude",lati);
                intent.putExtra("longitude",longi);
                setResult(3,intent);
                finish();
            }
        });
    }
}

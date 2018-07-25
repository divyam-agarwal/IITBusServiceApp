package com.example.avinashucheniya.bustracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class ArrivalActivity extends AppCompatActivity {

    Button btnSearch;
    AutoCompleteTextView stopFillTextView;

    String[] BUS_STOP_NAME = new String[]{"Pan Bazar",
            "Bharalukhmukh",
            "Kamkhya",
            "Maligaon",
            "Adabari Tiniali",
            "Jalukbari",
            "Amingaon",
            "Jayguru"};

    String bus_Stop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrival);

        btnSearch = findViewById(R.id.B_Search);
        stopFillTextView = findViewById(R.id.autoCompleteTextView);

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.select_dialog_item,BUS_STOP_NAME);
        stopFillTextView.setThreshold(1);
        stopFillTextView.setAdapter(adapter);

        takeBusStopName();
    }

    public void takeBusStopName(){
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus_Stop = stopFillTextView.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("Bus stop name",bus_Stop);
                setResult(2,intent);
                finish();
            }
        });
    }



}

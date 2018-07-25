package com.example.avinashucheniya.bustracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
   Name of Module :   DriverLogin.java

   Date on which the module was created :  24/03/2018

   Author's name :  Divyam Agrawal

   Modification History :   By Avinash Uchchainiya   26/03/2018
                            By Aditya Chouhan   29/04/2018

   Synopsis of the module : Module For Driver details authentication and store him location to online running bus database

   Functions in module :    void driverLogin()   No parameter,
                            void saveRunningBusData(String bus_id , String location)

   Global variables in module :

   Constant :         String[] DRIVER_DETAILS

*/



public class LoginActivity extends AppCompatActivity {

    Button btn_for_login;                                             // for fill driver detail
    EditText username,password;

    String[] DRIVER_DETAILS = new String[]{"driver1","password",             // Driver Details database
            "driver2","password",
            "driver3","password"};

    FirebaseDatabase running_buses_database;                                  // for online running database
    DatabaseReference running_bus_reference;                                            // for database reference

    // Function Execute when this Activity call

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        running_buses_database = FirebaseDatabase.getInstance();              // give instance to Firebase Database

        btn_for_login = (Button)findViewById(R.id.Btn_login);               // take value by user to given in text view
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        driverLogin();                                                 // call Driver login function for check credential of user
    }
  /*
    function for check driver authentication and check his details
   */

    public void driverLogin(){
        btn_for_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < DRIVER_DETAILS.length; i = i + 2) {
                    String driver_username = DRIVER_DETAILS[i];            // take driver username and password from database
                    String driver_password = DRIVER_DETAILS[i + 1];

                    if (username.getText().toString().equals(driver_username) &&                        // Authentication for driver detail
                            password.getText().toString().equals(driver_password)) {

                        String location = getIntent().getExtras().getString("Location");
                        saveRunningBusData(driver_username,location);            // call for save this location in running database

                        Toast.makeText(LoginActivity.this, "Username and password Correct", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this,LogoutActivity.class);      // create intent object
                        intent.putExtra("Driver_Username",driver_username);                  // start new Activity and give some extra value
                        startActivity(intent);
                        break;
                    } else {                                       // show message if Username and password wrong
                         Toast.makeText(LoginActivity.this, "Wrong username password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
          }
        );
    }

    /*
     Function For Save Driver location in running database
      input : String bus_id
              String location
      */

    public void saveRunningBusData(String bus_id , String location){

        running_bus_reference = running_buses_database.getReference(bus_id);            // get reference by Driver bus id
        running_bus_reference.setValue(location);   // set the location of driver or bus

        SharedPreferences local_database = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);   // Create object of SharedPreferences.
        SharedPreferences.Editor editor = local_database.edit();   //now get Editor
        editor.putBoolean("isLogin", true);                     // save value for use in previous class
        editor.putString("driver username",bus_id);
        editor.commit();                                           //commits your edits

    }

}

package com.example.avinashucheniya.bustracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
   Name of Module : MapsActivity.java

   Date on which the module was created :  25/03/2018

   Author's name :  Avinash Uchchainiya

   Modification History :   By Divyam Agrawal   28/03/2018
                            By Aditya Chouhan   01/04/2018

   Synopsis of the module : Main module which execute when app start and
                            show user location on Google Map and show asking
                            for any user task which my app provide

   Functions in module :   void Login()   No parameter
                           void nearestRunningBuss() No parameter
                           void showArrivalTimeOfBus() No parameter
                           void showArrivalTimeOfBus() No parameter
                           void showSuggestedBusStop() No parameter
                           void showTimeTable()        No parameter
                           void showMessage(String title, String message)
                           void getRunningBusData(String bus_id)
                           void showRunningBusesOnMap()  No parameter
                           void buildGoogleApiClient()   No parameter
                           boolean checkLocationPermission() No parameter
                           boolean onMarkerClick(Marker marker)
                           void onLocationChanged(Location location)
                           void onConnected(@Nullable Bundle bundle)

   Class used :
                           ShowNearestBusStop
                           GetArrivalTime

   Global variable in module :   No Variable

   Constants :
                              int REQUEST_LOCATION_CODE = 99
                              String INSTRUCTION_OF_APP
                              String[] DRIVER_DETAILS_DATA
   Naming Convention  :  local variables-small case
                         global variables-capital case
                         function name - camel case

*/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener{


    private GoogleMap map;
    // Variable For Google Map

    private GoogleApiClient googleapiclient;
    // Variable for Google Api and User Location

    private Location user_last_updated_location;
    // for user last updated location

    private Marker user_current_location_marker;
    // Variable for Marker Of User Current Location

    public static final int REQUEST_LOCATION_CODE = 99;

    double user_location_latitude, user_location_longitude;
    // Variable For user Location latitude and longitude

   // for selected bus location
    double selected_bus_latitude = 0.0;
    double selected_bus_longitude = 0.0;

    double user_destination_latitude, user_destination_longitude;

    boolean isuserlogin = false;
    // Variable For Driver is Login or not
    String running_bus = null;
    // if Driver Login then its name as Running Bus

    //  All Drivers Cradential for Login
    String[] DRIVER_DETAILS_DATA = new String[]{"driver1","password",
    "driver2","password",
    "driver3","password"};

     double[] running_buses_location = new double[6];
     //  Variable For Store Running Buses Location
     int total_running_buses = 0;
     // Variable For Count Toatal Running Buses


    FirebaseDatabase running_buses_database;
    // Variable For Online Database For Running Buses

    DatabaseReference running_bus_reference;


    String INSTRUCTION_OF_APP = "Blue Marker : User Location \nRed Marker : Bus Stop Location\nGreen Marker : Bus Location\nYellow Marker : Destination Location";

    /* Function execute when app Start
       input : savedinstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Condition For Check Supported Android Device
            checkLocationPermission();
            // Call Function for Location Permission
          }

        running_buses_database = FirebaseDatabase.getInstance();
        // Give instance to Online Running bus Database


        SharedPreferences local_database = PreferenceManager.getDefaultSharedPreferences(this);
        // taking Value of isuserlogin From Login Activity

        isuserlogin = local_database.getBoolean("isLogin", false);
        running_bus = local_database.getString("driver username",null);

        driverLogin();
        // Function For Driver Login

        showNearestBusStop();
        // Function For Nearest Bus Stops

        nearestRunningBuses();
        // Function For Nearest Running Buses

        showArrivalTimeOfBus();
        // Function For Arrival Time of Bus At given Bus Stop


        showSuggestedBusStop();
        // call function for suggested bus stop according user destination

        showTimeTable();

        showMessage("Instruction of App",INSTRUCTION_OF_APP);
        // Function For Show App Instruction

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

   /*
    Function For Login Of Driver for take location and it start login activity
   */

    public void driverLogin(){

        TextView btnForLogin = findViewById(R.id.B_login);
        // Taking Values For Login Button from xml

        btnForLogin.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Call On click view for Login button press

            Intent open_login_activity = new Intent(MapsActivity.this,LoginActivity.class);
            // send it to next Activity
            open_login_activity.putExtra("Location", user_location_latitude +","+ user_location_longitude);
            // send required details
            startActivity(open_login_activity);
                    }
                }
        );
    }

    /*
       Function For Show Nearest Running Buses from user location
     */

    public void nearestRunningBuses(){
        TextView btn_for_nearest_buses = findViewById(R.id.B_buses);
        // Taking Values For Nearest bus Button from xml

        btn_for_nearest_buses.setOnClickListener( new View.OnClickListener() {      // Call Button Call Listener
                @Override
                public void onClick(View v) {
                    for(int i = 0; i < DRIVER_DETAILS_DATA.length; i = i+2){
                        // Loop For Check All buses wheater it is Running or not
                        String bus_id = DRIVER_DETAILS_DATA[i];
                        getRunningBusData(bus_id);
                    }
                    showRunningBusesOnMap();
                    // Show All Running Buses On Map
                }
            }
        );
    }

    /*
       Function For Store Running bus Data from Running Bus Database in array for show on map
        input : bus_id for check it is running or not
     */

    public void getRunningBusData(String bus_id){
        running_bus_reference = running_buses_database.getReference(bus_id);
        running_bus_reference.addValueEventListener(new ValueEventListener() {
            // Take Data From Online Running Buses Database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // Check Bus Is Running or Not
                    String value = dataSnapshot.getValue(String.class);
                    String[] seperated = value.split(",");

                    // Store Location of Running Buses in running_buses_location Array
                    running_buses_location[total_running_buses] = Double.parseDouble(seperated[0]);
                    running_buses_location[total_running_buses + 1] = Double.parseDouble(seperated[1]);

                    total_running_buses = total_running_buses + 2;
                    // Increase Total Running Buses Counter
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /*
      Function For Show All Running Buses On Google Map
     */

   public void showRunningBusesOnMap(){
        for(int i = 0; i< total_running_buses; i = i+2){
            // Loop for Take All Running Buses

            MarkerOptions markerOptions = new MarkerOptions();
            // Set Position Of Marker According their Location
            markerOptions.position(new LatLng(running_buses_location[i], running_buses_location[i+1]));
            markerOptions.title("Bus");
            // Set Title on Marker
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(running_buses_location[i],running_buses_location[i+1])));

            map.addMarker(markerOptions);
            // Add Marker To Google Map
        }
   }

    /*
       Function For Showing Alert Message to show some message
        input : String title  for title of alert message
                String message  for message to show
     */

    public void showMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Create Alert Dialog
        builder.setCancelable(true);
        builder.setTitle(title);
        // Set Title And Message
        builder.setMessage(message);
        builder.show();
        // Show the Alert Dialog
    }



    /*
       Function For Show Nearest Bus Stop according to user destination
      */

   public void showSuggestedBusStop() {
        TextView  btn_for_suggested_busstop =findViewById(R.id.B_suggested);
        // Taking Values For Suggested bus stop Button from xml

        btn_for_suggested_busstop.setOnClickListener( new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              // Take All Bus Stop Details From Bus Stop Database

              Intent intent = new Intent(MapsActivity.this,SuggestedBusStopActivity.class); // Start New Activity For Take Take Name Of Bus Stop
              startActivityForResult(intent,3);
          }
      });
    }



     // Function For Show Arrival Time Of selected Bus At Any Bus Stop


     public void showArrivalTimeOfBus(){

         TextView btn_for_arrival_time = findViewById(R.id.B_arrival);
             btn_for_arrival_time.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                 map.setOnMarkerClickListener(MapsActivity.this);    // set Property of Google Map to click on Marker for select Bus
                     Intent intent = new Intent(MapsActivity.this,ArrivalActivity.class); // Start New Activity For Take Take Name Of Bus Stop
                     startActivityForResult(intent,2);
                 }
                 });
         }

             // Function For Take Name of Bus Stop For Checking Selected Bus Arrival Time To There

     @Override

     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if(requestCode == 2 && resultCode == 2){                                 // Condition For Checking Correct Activity
                 String bus_stop_name = data.getStringExtra("Bus stop name");  //  Take Bus Stop Name From Activity

                 map.setOnMarkerClickListener(MapsActivity.this);          // set Property of Google Map to click on Marker for select Bus

                 if(selected_bus_latitude != 0.0 && selected_bus_longitude != 0.0) {          // Check Selected Bus Location is Right or Not

                     GetArrivalTime getArrivalTime = new GetArrivalTime();  // Create Object Of Arrival Time Class
                     Object dataTransfer[] = new Object[4];

                     dataTransfer[0] = map;                           // Send Required Data For Calculate Arrival Time To Class
                     dataTransfer[1] = bus_stop_name;
                     dataTransfer[2] = selected_bus_latitude;
                     dataTransfer[3] = selected_bus_longitude;

                     getArrivalTime.execute(dataTransfer);             // Call GetArrivalTime Class Function for calculate Time
                 }else{
                     Toast.makeText(this,"Not Select Any Bus",Toast.LENGTH_SHORT).show();  // Show Message when user did not select bus
                 }
             }
             if(requestCode == 3 && resultCode == 3){

             String latitude = data.getStringExtra("latitude");
             String longitude = data.getStringExtra("longitude");

                 user_destination_latitude = Double.valueOf(latitude);
                 user_destination_longitude = Double.valueOf(longitude);

                 MarkerOptions markerOptions = new MarkerOptions();
                 markerOptions.position(new LatLng(user_destination_latitude,user_destination_longitude));
                 markerOptions.title("Destination");
                 markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                 map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(user_destination_latitude,user_destination_longitude)));


                 map.addMarker(markerOptions);

                 ShowNearestBusStop show_nearest_bus_stop = new ShowNearestBusStop();
                 // Create Object Of Showing Nearest Bus Stop Class
                 Object dataTransferForSuggestedBusStop[] = new Object[4];

                 dataTransferForSuggestedBusStop[0] = map;
                 //  Send Required Data For Showing Nearest Bus Stop
                 dataTransferForSuggestedBusStop[2] = user_destination_latitude;
                 dataTransferForSuggestedBusStop[3] = user_destination_longitude;

                 show_nearest_bus_stop.execute(dataTransferForSuggestedBusStop);
                 // Call Function of NearestBusStop Class For Showing Nearest Bus Stop

         }
     }

    /*
        Function For Show Nearest Bus Stop From user Location
      */

    public void showNearestBusStop() {
        TextView btn_for_nearest_stop = findViewById(R.id.B_stop);
        // Taking Value For nearest bus stop Button from xml

        btn_for_nearest_stop.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Take All Bus Stop Details From Bus Stop Database

                    int busStopCount = 0;

                    ShowNearestBusStop show_nearest_bus_stop = new ShowNearestBusStop();
                    // Create Object Of Showing Nearest Bus Stop Class
                    Object dataTransferForNearestBusStop[] = new Object[4];

                    dataTransferForNearestBusStop[0] = map;
                    //  Send Required Data For Showing Nearest Bus Stop
                    dataTransferForNearestBusStop[2] = user_location_latitude;
                    dataTransferForNearestBusStop[3] = user_location_longitude;

                    show_nearest_bus_stop.execute(dataTransferForNearestBusStop);
                    // Call Function of NearestBusStop Class For Showing Nearest Bus Stop
                }
            }
        );
    }




    /*
       Function For Show Time table and Route of IITG Bus Service
     */

   public void showTimeTable(){
       TextView  btn_for_time_table = findViewById(R.id.B_timetable);
       // Taking Value For Time Table Button from xml

       btn_for_time_table.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent open_time_table_activity = new Intent
                     (MapsActivity.this,ShowTimeTableActivity.class);
             // send Show Time table Activity it
             startActivity(open_time_table_activity);
             // And Start and show time table and route
         }
     });
   }

    /*
       Function For Check Permission  Of Android Device For Location
        input : int requestCode for location request code by internet
        String[] permissions  for set permission
        int[] grantResults   for result of each request
      */

    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // Check For Permission in Manifest file Of App
                {
                    if(ContextCompat.checkSelfPermission
                            (this,Manifest.permission.ACCESS_FINE_LOCATION)
                            !=  PackageManager.PERMISSION_GRANTED) {
                        if(googleapiclient == null) {
                            // Check Google Api Client Created Or Not
                            buildGoogleApiClient();
                            // Call function for build Google Api Client if it is null
                        }
                        map.setMyLocationEnabled(true);
                        // Set property of Google map for Showing User Location on Map
                    }
                }
                else {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                    // Show Message if Permission not Granted
                }
        }
    }

    /*
      Function For Build Google Api Client
       input : GoogleMap googleMap  for location show on map
      */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Check Permission By User
        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            // Build Google Api Client For User Location
            map.setMyLocationEnabled(true);
            map.setOnMarkerClickListener(this);
        }

    }

  /*
       Function for Build Google Api Client and Connect it to internet
    */

    protected synchronized void buildGoogleApiClient() {
        //  Build Google Api Client and set Some Properties
        googleapiclient = new GoogleApiClient.Builder
                (this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleapiclient.connect();
        // Connect client to Internet

    }

    /*
     Function For Save Running Bus Data in Data base When Location Bus Change
      input : bus_id  for store its bus id for get bus
              String location  save its location
      */

    public void saveRunningBusData(String bus_id , String location){

        running_bus_reference = running_buses_database.getReference(bus_id);
        running_bus_reference.setValue(location);

    }

    /*
       Function For Update Location when User Location Change
       Location location  for change for marker location on map
      */

    @Override
    public void onLocationChanged(Location location) {

        user_location_latitude = location.getLatitude();
        //  Store Latitude and Longitude of user updated Location
        user_location_longitude = location.getLongitude();
        user_last_updated_location = location;
        // Store User Updated location

        if(isuserlogin && running_bus != null){
            // check if User is Login or not

            saveRunningBusData(running_bus, user_location_latitude +","+ user_location_longitude);
            // Change Location of Running Bus if Login
        }

        if(user_current_location_marker != null) {
            // Remove Previous Marker
            user_current_location_marker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        // Set position of New Marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        // Set Title of Marker
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        user_current_location_marker = map.addMarker(markerOptions);
        // Add Marker To Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomBy(10));
        // Zoom Camera on User Location Marker

        if(googleapiclient != null) {
            // Condition For Remove Location Update
            LocationServices.FusedLocationApi.removeLocationUpdates(googleapiclient,this);
        }
    }

    /*
     Function For Request Location Update From Internet
      input : Bundle bundle for connection check and location update
      */

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

      LocationRequest locationrequest = new LocationRequest();
        locationrequest.setInterval(100);
        // Set Location Update Interval
        locationrequest.setFastestInterval(1000);
        locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set Priority Of User location update request

        // Check For Permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates
                    (googleapiclient, locationrequest, this);
            // Request For Location Update
        }
    }

    /*
     Function For Check Location Permission By User in him android device
      */

    public boolean checkLocationPermission() {                                                                                             // Check Permission in Manifest file of App
        if(ContextCompat.checkSelfPermission
                (this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED ) {

            // Set Permission if not Given
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }
    /*
       function for take marker location when click on it
    */

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Function For Marker Click

       // Toast.makeText(this,"Bus is Selected",Toast.LENGTH_SHORT).show();
        selected_bus_latitude = marker.getPosition().latitude;
        // Take Position Of Selected Marker in latitude and longitude
        selected_bus_longitude = marker.getPosition().longitude;
        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}

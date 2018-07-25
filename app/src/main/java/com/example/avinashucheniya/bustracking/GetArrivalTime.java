package com.example.avinashucheniya.bustracking;

import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Avinash Ucheniya on 21-Apr-18.
 */

/*
   Name of Module :  GetArrivalTime.java

   Date on which the module was created :  02/04/2018

   Author's name :  Aditya Chouhan

   Modification History :   By Divyam Agrawal   04/03/2018
                            By Avinash Uchchainiya   06/04/2018

   Synopsis of the module :  this Module Give arrival time of user selected bus to bus stop which is selected by user

   Functions in module :  String doInBackground(Object... objects)
                          String getGoogleApiUrl()         No parameter
                          void onPostExecute(String[] s)
   External classes used :
                              Dataparser
                              DownloadURL

   Global variable in module :  no global variable

       Constants :              String BUS_STOP_NAME;
                                String[] BUS_STOP_DATA
*/


public class GetArrivalTime extends AsyncTask<Object, String,String> {

    private GoogleMap map;                            // Google Map variable For Showing Bus Bus Stop On it

    private double bus_stop_location_latitude = 0.0;           // variable for bus Stop locations given by user
    private double bus_stop_location_longitude = 0.0;

    private double selected_bus_latitude, selected_bus_longitude;     // for Selected bus location

    private String BUS_STOP_NAME;                               // for store user selected bus stop

    private String[] BUS_STOP_DATA = new String[]{"26.186057","91.749133","Pan Bazar",     //  All Bus Stop Data as Longitude,Latitude and Name
            "26.175478","91.731563","Bharalukhmukh",
            "26.165343","91.716637","Kamkhya",
            "26.159006","91.695087","Maligaon",
            "26.158449","91.685531","Adabari Tiniali",
            "26.157948","91.674481","Jalukbari",
            "26.185097","91.667111","Amingaon",
            "26.210156","91.688669","Jayguru"};

    // Function Take Data And Details From Google Api To give duration bus

    @Override
    protected String doInBackground(Object... objects) {

        map = (GoogleMap)objects[0];                        // Store Required Data From Super Class
        BUS_STOP_NAME = (String) objects[1];
        selected_bus_latitude = (double)objects[2];
        selected_bus_longitude = (double)objects[3];

        for(int i=2; i<BUS_STOP_DATA.length; i = i+3 ){            // Loop For Getting location of user selected Bus Stop

            if(BUS_STOP_DATA[i] == BUS_STOP_NAME){

                String latitude = BUS_STOP_DATA[i-2];              // taking selected bus stop location
                String longitude = BUS_STOP_DATA[i-1];

                bus_stop_location_latitude = Double.valueOf(latitude);        // Store it into variable after convert in double
                bus_stop_location_longitude = Double.valueOf(longitude);

                break;
            }
        }

        String google_directions_data = null;                       // Variable for Store Google direction Data From Google Api

               // Checking for selected bus stop exist or not

            String url = getGoogleApiUrl();                                  // Call Function For Store take url For get Direction Data
            DownloadURL downloadURL = new DownloadURL();
            try {
                google_directions_data = downloadURL.readUrl(url);             // Take Google Direction Data From Api and internet URL
            } catch (IOException e) {
                e.printStackTrace();
            }


        return google_directions_data;                                   // return google Direction Data of all bus stop
    }

    /*
      Function For Get url For Google Api to take direction data
       */

    private String getGoogleApiUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+ selected_bus_latitude +","+ selected_bus_longitude);              // Build Google Url For Take data
        googleDirectionsUrl.append("&destination="+ bus_stop_location_latitude +","+ bus_stop_location_longitude);
        googleDirectionsUrl.append("&key="+"AIzaSyBPNij0wjJ-_Qm1JqIesHCWxRXGxT2phlc");

        return googleDirectionsUrl.toString();                                                          // return url
    }

    /*
       Function For calculate Arrival time of bus and on Map
      */

    @Override
    protected void onPostExecute(String s){

        if(s != null) {
            HashMap<String, String> directions_data_list = null;     // Variable For store json object
            DataParser json_data_by_url = new DataParser();              // Object of data parser

            directions_data_list = json_data_by_url.parseDirections(s);      // take data from jsom object
            String duration = directions_data_list.get("duration"); // take duration of bus

            LatLng latLng = new LatLng(bus_stop_location_latitude, bus_stop_location_latitude);

            MarkerOptions markerOptions = new MarkerOptions();     // Showing Bus Stop on Map and Arrival time
            markerOptions.position(latLng);
            markerOptions.title(BUS_STOP_NAME);                    // set title of marker
            markerOptions.snippet("Arrival Time : " + duration);

            map.addMarker(markerOptions);                         // add marker on map

        }
    }

}

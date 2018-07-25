package com.example.avinashucheniya.bustracking;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Avinash Ucheniya on 07-Apr-18.
 */
/*
   Name of Module :  ShowNearestBusStopFromOrigin.java

   Date on which the module was created :  20/03/2018

   Author's name :  Avinash Uchchainiya

   Modification History :   By Divyam Agrawal   25/03/2018
                            By Aditya Chouhan   28/04/2018

   Synopsis of the module : Module For Take Google Direction Api Data According to origin and destination latitude and longitude and
                            show nearest bus stop from origin location

   Functions in module :  void String[] doInBackground(Object... objects)
                          void getGoogleApiUrl(String destinationLatitude,String destinationLongitude)
                          void onPostExecute(String[] s)
                          void showBusStopOnMap(LatLng positionofBusStop,String duration,String distance)
                          void displayPathOfBusStop(String[] directionPathList)
   External Class used :
                          Dataparser
                          DownLoadURL

   Global variable in module :  No Variable

   Constants :    String[] BUS_STOP_DATA
*/

public class ShowNearestBusStop extends AsyncTask<Object, String, String[]> {

    private GoogleMap map;                            // Google Map variable For Showing Bus Bus Stop On it

    private double origin_location_latitude, origin_location_longitude;     // for user current location

   private double[] bus_stop_latitude_array = new double[8];              // For Store Bus Stop Location Data From Bus Stop Database
   private double[] bus_stop_longitude_array = new double[8];

   private String[] BUS_STOP_DATA = new String[]{"26.186057","91.749133","Pan Bazar",     //  All Bus Stop Data as Longitude,Latitude and Name
            "26.175478","91.731563","Bharalukhmukh",
            "26.165343","91.716637","Kamkhya",
            "26.159006","91.695087","Maligaon",
            "26.158449","91.685531","Adabari Tiniali",
            "26.157948","91.674481","Jalukbari",
            "26.185097","91.667111","Amingaon",
            "26.210156","91.688669","Jayguru"};

   /*
    Function Take Data And Details From Google Api url
    input :  Object... objects  take data from previous class
     */

    @Override
    protected String[] doInBackground(Object... objects) {

        map = (GoogleMap)objects[0];                             // Store Required Data From Super Class
        origin_location_latitude = (double)objects[2];
        origin_location_longitude = (double)objects[3];

        String google_directions_data = null;                       // Variable for Store Google direction Data From Google Api
        String[] google_directions_data_array = new String[8];

        int busstopcount = 0,google_direction_data_count = 0;        // for total bus stop and google direction count

        while(busstopcount < BUS_STOP_DATA.length) {

           String latitude = BUS_STOP_DATA[busstopcount];           // Take each Bus stop Data
           String longitude = BUS_STOP_DATA[busstopcount+1];

            bus_stop_latitude_array[google_direction_data_count] = Double.valueOf(latitude);     // Store it into bus stop location Array
            bus_stop_longitude_array[google_direction_data_count] = Double.valueOf(longitude);

            String url = getGoogleApiUrl(latitude,longitude);                    // Call Function For Store take url For get Direction Data
            DownloadURL downloadURL = new DownloadURL();
            try {
                google_directions_data = downloadURL.readUrl(url);   // Take Google Direction Data From Api and internet URL
            } catch (IOException e) {
                e.printStackTrace();
            }
          google_directions_data_array[google_direction_data_count] = google_directions_data;  // Store Google Direction Data of Each Bus Stop
          busstopcount = busstopcount + 3;                                     // increment in busStopCount and google direction count
          google_direction_data_count++;
        }
        return google_directions_data_array;               // return google Direction Data of all bus stop
    }

    /*
     Function For Get url For Google Api to take direction data

      */

    private String getGoogleApiUrl(String destinationLatitude,String destinationLongitude ) {
        StringBuilder googledirectionsurl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googledirectionsurl.append("origin="+ origin_location_latitude +","+ origin_location_longitude);               // Build Google Url For Take data
        googledirectionsurl.append("&destination="+destinationLatitude+","+destinationLongitude);
        googledirectionsurl.append("&key="+"AIzaSyBPNij0wjJ-_Qm1JqIesHCWxRXGxT2phlc");

        return googledirectionsurl.toString();          // return url
    }

    /*
       Function For calculate distance and duration of each bus stop and Show nearest Bus Stop on Map
       input : String[] google_direction_data  for take duration and distance
      */

    @Override
    protected void onPostExecute(String[] google_direction_data){

        int j=2;
        for(int i=0; i<google_direction_data.length; i++) {                 //  Take Each google direction data
          String[] directionPathList;                     // variable for store polyline to show path between user and bus stop
          HashMap<String, String> directionslist = null;
          DataParser json_data_by_url = new DataParser();

          directionslist = json_data_by_url.parseDirections(google_direction_data[i]);    // take data from json object From internet for duration and distance

          String duration = directionslist.get("duration");   // take duration and distance
          String distance = directionslist.get("distance");

          directionPathList = json_data_by_url.parseDirectionPath(google_direction_data[i]);   // take Direction path list for show path

          String[] seperated = distance.split(" ");

          if (Double.valueOf(seperated[0]) < 5.0) {             // show bus stop if distance less than 5 km

              displayPathOfBusStop(directionPathList);             // Call Function For Path of bus stop
              LatLng latLng = new LatLng(bus_stop_latitude_array[i], bus_stop_longitude_array[i]);   // Showing Bus Stop on Map
              showBusStopOnMap(latLng,duration,distance,BUS_STOP_DATA[j]);// call function For resultant bus stop on map
          }
          j = j+3;
      }
    }

    /*
       Function for Show Bus Stop On Google map
       input : LatLng positionofbusstop  for postion of bus stop
               String duration
               String distance
       */
    public void showBusStopOnMap(LatLng positionofbusstop,String duration,String distance,String busstop){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(positionofbusstop);
        markerOptions.draggable(true);
        markerOptions.title(busstop);            // set Duration and distance on marker
        markerOptions.snippet("Distance: " + distance + "Duration: "+duration);

        map.addMarker(markerOptions);                          // Add marker on map

    }


    /*
     Function For Display Path of Bus stop from user
       input : String[] directionpathlis     for show all points
      */

    public void displayPathOfBusStop(String[] directionpathlist) {
        int count = directionpathlist.length;

        for(int i=0; i<count; i++) {
            PolylineOptions options = new PolylineOptions();   // Add polyline On map
            options.color(Color.RED);
            options.width(10);                                     // set Some property of map
            options.addAll(PolyUtil.decode(directionpathlist[i]));

            map.addPolyline(options);
        }
    }

}

package com.example.avinashucheniya.bustracking;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Avinash Ucheniya on 29-Mar-18.
 */

/*
   Name of Module :      DownloadDataFromURL.java

   Date on which the module was created :  25/03/2018

   Author's name :  Avinash Uchchainiya

   Modification History :   By Divyam Agrawal   28/03/2018
                            By Aditya Chouhan   01/04/2018

   Synopsis of the module :  This module use for download the data from internet by using google direction Api url

   Functions in module :    readUrl( String googleDirectionData )   return type String

   Global variable in module :    no global variable

*/

public class DownloadURL {

    /*
       Function For Download Direction Data From Given Url
       input : String google_api_url
      */

    public String readUrl(String google_api_url) throws IOException
    {
        String google_direction_data = "";
        InputStream input_stream = null;                      // Declare input stream and UrL Connection for connect to internet
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(google_api_url);                           // take String url and Change internet url
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.connect();                                          // Connect url to Internet

            input_stream = urlConnection.getInputStream();                // take Input Stream From Internet and store

            BufferedReader br = new BufferedReader(new InputStreamReader(input_stream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null) {          // store input reader in string buffer
                sb.append(line);
            }

            google_direction_data = sb.toString();                        // change string buffer to string
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            input_stream.close();                        // close internet connection

        }
        Log.d("DownloadURL","Returning data= "+google_direction_data);

        return google_direction_data;                                  // Send Data to super class
    }
}

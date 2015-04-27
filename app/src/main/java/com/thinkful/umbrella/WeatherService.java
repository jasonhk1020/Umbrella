package com.thinkful.umbrella;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
* Created by jasonhk1020 on 4/27/2015.
*/
public class WeatherService{
    public WeatherService(){
    }
    public String getWeather(String latitude, String longitude) {

        String useUmbrellaStr = "Don't know";
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&cnt=1&mode=json&units=metric");
            Log.i("getWeather",url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            useUmbrellaStr = useUmbrella(urlConnection.getInputStream());
        } catch (IOException e) {
            Log.e("MainActivity", "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return useUmbrellaStr;
    }

    protected String useUmbrella(InputStream in) {

        //defaults
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        //read and parse InputStream
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            //JSON parse
            JSONObject forecastJson = new JSONObject(stringBuilder.toString());
            JSONArray weatherArray = forecastJson.getJSONArray("list");
            JSONObject todayForecast = weatherArray.getJSONObject(0);

            if (todayForecast.has("rain") || todayForecast.has("snow")) {
                return ("Yes");
            } else {
                return ("No");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return "cheesecake";
    }

}


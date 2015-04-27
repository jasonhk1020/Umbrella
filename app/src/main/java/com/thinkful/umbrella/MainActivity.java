package com.thinkful.umbrella;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start connection to google maps api=
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        // get last location
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //update with the new location every 60 seconds with highest accuracy
        startLocationUpdates(mGoogleApiClient);

        //perform in backgroun
        WebServiceTask webserviceTask = new WebServiceTask();
        webserviceTask.execute(String.valueOf(mCurrentLocation.getLatitude()),String.valueOf(mCurrentLocation.getLongitude()));
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void startLocationUpdates(GoogleApiClient mGoogleApiClient) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    private class WebServiceTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            //set the default values
            String useUmbrellaStr = "Don't know, sorry about that.";

            HttpURLConnection urlConnection = null;

            try{
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat="+params[0]+"&lon="+params[1]+"&cnt=1&mode=json&units=metric");
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
                //JSON needs to be parsed here

                JSONObject forecastJson = new JSONObject(stringBuilder.toString());
                JSONArray weatherArray = forecastJson.getJSONArray("list");
                JSONObject todayForecast = weatherArray.getJSONObject(0);

                if (todayForecast.has("rain") || todayForecast.has("snow")) {
                    Log.i("Returned data", stringBuilder.toString());
                    return("Yes, but here's today's temperature forecast: " + todayForecast.getString("temp"));

                } else {
                    return("No, but here's today's temperature forecast: " + todayForecast.getString("temp"));
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
            return "Don't know, sorry about that.";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView textView = (TextView) findViewById(R.id.hello);
            textView.setText("Should I take an umbrella today? "+s);

        }
    }
}

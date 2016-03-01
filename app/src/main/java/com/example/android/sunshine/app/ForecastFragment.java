package com.example.android.sunshine.app;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ZMthis on 2016-02-29.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }
    private ArrayAdapter<String> forecastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh) {
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(59.53, 18.09);
            fetchWeatherTask.execute();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);



        ArrayList<String> forecastList = new ArrayList<>();
        forecastList.add("WEATHER MOTHERFUCKERS");
        forecastList.add("MORE WEATHER");
        forecastList.add("ITS RAINING SNACKBARS");
        forecastList.add("MOCK");


        //FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(59.53, 18.09);
        //fetchWeatherTask.execute();
        forecastAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, forecastList);

        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(forecastAdapter);

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  Toast.makeText(getActivity().getApplicationContext(), "This is a muddafucking toast", Toast.LENGTH_SHORT).show();
              }
          });
        return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private double latitude, longitude;

        public FetchWeatherTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String[] doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJson = null;
            String[] strs;

            try {
                Uri.Builder uri = new Uri.Builder();
                uri.scheme("http").authority("api.openweathermap.org").appendPath("data").appendPath("2.5").appendPath("forecast")
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt", "7")
                        .appendQueryParameter("appid", "a1205a5df186916a78fe44a4705ca2a6")
                        .appendQueryParameter("lat", String.valueOf(latitude))
                        .appendQueryParameter("lon", String.valueOf(longitude));

                URL url = new URL(uri.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null) {
                    return null;
                }
                StringBuffer stringBuffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if(stringBuffer.length() == 0) {
                    return null;
                }

                strs = getWeatherDataFromJson(stringBuffer.toString(), 5);
            } catch(Exception e) {
                Log.e(LOG_TAG, e.toString());
                return null;
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch(Exception e) {
                        Log.e(LOG_TAG, "error closing reader", e);
                    }
                }
            }

            return strs;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            forecastAdapter.clear();
            if(strings != null) {
                //forecastAdapter.addAll(Arrays.asList(strings));
                for (String s : strings) {
                    forecastAdapter.add(s);
                }
            }
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            JSONObject obj = new JSONObject(forecastJsonStr);
            JSONArray list = obj.getJSONArray("list");

            String[] strs = new String[numDays];

            for(int i = 0; i < numDays; i++) {
                JSONObject current = list.getJSONObject(i);

                String temp = (current.getJSONObject("main").getDouble("temp_min")) + "/" + (current.getJSONObject("main").getDouble("temp_max"));

                String description = current.getJSONArray("weather").getJSONObject(0).getString("description");

                Time dayTime = new Time();
                dayTime.setToNow();
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
                dayTime = new Time();
                long dateTime = dayTime.setJulianDay(julianStartDay+i);
                String day = new SimpleDateFormat("EEE MMM dd").format(dateTime);

                strs[i] = day + "    " + description + "    " + temp;
            }
            return strs;
        }
    }
}
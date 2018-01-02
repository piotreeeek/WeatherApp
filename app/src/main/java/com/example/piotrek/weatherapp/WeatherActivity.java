package com.example.piotrek.weatherapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    private JSONObject data = null;
    private TextView cityTemp = null;
    private TextView cityWeather = null;
    private TextView cityHumidity = null;
    private TextView cityPressure = null;
    private TextView cityWind = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Bundle bundle = getIntent().getExtras();

        TextView cityName = findViewById(R.id.city_name);
        cityTemp = findViewById(R.id.city_temp);
        cityWeather = findViewById(R.id.city_weather);
        cityHumidity = findViewById(R.id.city_humidity);
        cityPressure = findViewById(R.id.city_pressure);
        cityWind = findViewById(R.id.city_wind);

        String city = (String) bundle.get(getString(R.string.city_variable_name));
        cityName.setText(getString(R.string.city_name) + " " + city);

        getJSON(city);

    }

    @SuppressLint("StaticFieldLeak")
    private void getJSON(final String city) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&APPID=609191e92beb5b9b8d10c2124c026211");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder json = new StringBuilder(1024);
                    String tmp;

                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());

                    if (data.getInt("cod") != 200) {
                        return null;
                    }


                } catch (Exception e) {

                    System.out.println("Exception " + e.getMessage());
                    cityTemp.setText(R.string.error_dl_weather);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if (data != null) {
                    Log.d("my weather received", data.toString());
                    try {
                        JSONObject mainData = new JSONObject(data.get("main").toString());
                        JSONArray weatherArray = new JSONArray(data.get("weather").toString());
                        JSONObject weatherData = new JSONObject(weatherArray.get(0).toString());
                        JSONObject windData = new JSONObject(data.get("wind").toString());

                        cityTemp.setText(getString(R.string.city_temp) + " " + mainData.get("temp").toString() + " " + getString(R.string.degrees));
                        cityWeather.setText(getString(R.string.city_weather) + " " + weatherData.get("main").toString());
                        cityHumidity.setText(getString(R.string.city_humidity) + " " + mainData.get("humidity").toString() + " %");
                        cityPressure.setText(getString(R.string.city_pressure) + " " + mainData.get("pressure").toString() + " " + getString(R.string.hpa));
                        cityWind.setText(getString(R.string.city_wind) + " " + windData.get("speed").toString() + " " + getString(R.string.wind_unit));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute();
    }
}


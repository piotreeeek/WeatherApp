package com.example.piotrek.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    private static final String IMG_URL = "http://openweathermap.org/img/w/";
    private TextView cityTemp = null;
    private ImageView cityIcon = null;
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
        cityIcon = findViewById(R.id.city_icon);
        cityTemp = findViewById(R.id.city_temp);
        cityWeather = findViewById(R.id.city_weather);
        cityHumidity = findViewById(R.id.city_humidity);
        cityPressure = findViewById(R.id.city_pressure);
        cityWind = findViewById(R.id.city_wind);

        assert bundle != null;
        String city = (String) bundle.get(SelectActivity.CITY_VARIABLE_NAME);
        cityName.setText(getString(R.string.city_name, city));

        WeatherDownloader.OnDownloadListener downloadListener = new WeatherDownloader.OnDownloadListener() {
            @Override
            public void OnDownloadFinish(JSONObject response) {
                weatherInfoProcess(response);
            }
        };

        WeatherDownloader mTask = new WeatherDownloader(downloadListener, city);
        mTask.execute();

    }

    private void weatherInfoProcess(JSONObject jsonObject) {

        if (jsonObject != null && isNetworkAvailable()) {
            Log.d("my weather received", jsonObject.toString());
            try {
                JSONObject mainData = new JSONObject(jsonObject.get("main").toString());
                JSONArray weatherArray = new JSONArray(jsonObject.get("weather").toString());
                JSONObject weatherData = new JSONObject(weatherArray.get(0).toString());
                JSONObject windData = new JSONObject(jsonObject.get("wind").toString());

                Picasso.with(this).load(IMG_URL + weatherData.get("icon").toString() + ".png")
                        .into(cityIcon);

                cityTemp.setText(getString(R.string.city_temp, mainData.get("temp").toString()));
                cityWeather.setText(getString(R.string.city_weather, weatherData.get("main").toString()));
                cityHumidity.setText(getString(R.string.city_humidity, mainData.get("humidity").toString()));
                cityPressure.setText(getString(R.string.city_pressure, mainData.get("pressure").toString()));
                cityWind.setText(getString(R.string.city_wind, windData.get("speed").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            cityTemp.setText(getText(R.string.error_dl_weather));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager
                .getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

class WeatherDownloader extends AsyncTask<Void, Void, Void> {

    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String API_UNITS_KEY = "&units=metric&APPID=609191e92beb5b9b8d10c2124c026211";
    private final OnDownloadListener listener;
    private final String city;
    private JSONObject data = null;

    public WeatherDownloader(OnDownloadListener listener, String city) {
        this.listener = listener;
        this.city = city;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            URL url = new URL(API_URL + city + API_UNITS_KEY);

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

            Log.d("Exception ", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void Void) {

        if (listener != null) {
            listener.OnDownloadFinish(data);
        }

    }

    interface OnDownloadListener {
        void OnDownloadFinish(JSONObject response);
    }
}


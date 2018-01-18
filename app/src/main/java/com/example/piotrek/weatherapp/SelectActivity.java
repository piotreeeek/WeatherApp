package com.example.piotrek.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String CITY_VARIABLE_NAME = "city";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        listView = findViewById(R.id.list_view_city);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(this, WeatherActivity.class);
        intent.putExtra(CITY_VARIABLE_NAME, (String) listView.getItemAtPosition(position));
        startActivity(intent);
    }
}

package com.yepsolutions.myv600application;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.yepsolutions.myv600application.home_page.HelpActivity;
import com.yepsolutions.myv600application.home_page.ManageCreditActivity;
import com.yepsolutions.myv600application.home_page.MapActivity;
import com.yepsolutions.myv600application.home_page.ScheduleActivity;
import com.yepsolutions.myv600application.home_page.SettingsActivity;
import com.yepsolutions.myv600application.home_page.UserActivity;

public class MainActivity extends AppCompatActivity {


    private ImageView button_Manage_Credit;
    private ImageView button_Schedule;
    private ImageView button_User;
    private ImageView button_Settings;
    private ImageView button_Help;
    private ImageView button_Map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);


        button_Manage_Credit = findViewById(R.id.img_buy_ticket);
        button_Schedule = findViewById(R.id.img_schedule);
        button_User = findViewById(R.id.img_user);
        button_Settings = findViewById(R.id.img_settings);
        button_Help = findViewById(R.id.img_help);
        button_Map = findViewById(R.id.img_map);

        button_Manage_Credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ManageCreditActivity.class));
            }
        });

        button_Schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
            }
        });

        button_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (MainActivity.this, UserActivity.class));
            }
        });

        button_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (MainActivity.this, MapActivity.class));
            }
        });

        button_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (MainActivity.this, SettingsActivity.class));
            }
        });

        button_Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (MainActivity.this, HelpActivity.class));
            }
        });

    }

}



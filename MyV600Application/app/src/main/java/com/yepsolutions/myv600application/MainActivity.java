package com.yepsolutions.myv600application;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.yepsolutions.myv600application.home_page.HelpActivity;
import com.yepsolutions.myv600application.home_page.ManageCreditActivity;
import com.yepsolutions.myv600application.home_page.MapActivity;
import com.yepsolutions.myv600application.home_page.ScheduleActivity;
import com.yepsolutions.myv600application.home_page.SettingsActivity;
import com.yepsolutions.myv600application.home_page.UserActivity;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
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

        //Configurando toolbar
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);

        //botao voltar
        getActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão


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
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        button_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });

        button_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        button_Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //toobar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_setings:
                return true;
            case R.id.id_action_log_out:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void logOut() {
        Toast toast = Toast.makeText(this , "Sair", Toast.LENGTH_SHORT);
        toast.show();
    }
}




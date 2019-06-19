package com.yepsolutions.myv600application.home_page;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yepsolutions.myv600application.MainActivity;
import com.yepsolutions.myv600application.R;

public class SettingsActivity extends AppCompatActivity {

    private Button button_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        button_home = findViewById(R.id.id_button_userPage);

        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });

    }
}

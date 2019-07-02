package com.yepsolutions.myv600application.home_page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.yepsolutions.myv600application.MainActivity;
import com.yepsolutions.myv600application.R;

public class HelpActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button button_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        getActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão

        button_home = findViewById(R.id.id_button_userPage);

        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HelpActivity.this, MainActivity.class));
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

}

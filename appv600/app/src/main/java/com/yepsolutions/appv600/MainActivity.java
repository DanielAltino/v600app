package com.yepsolutions.appv600;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLogin = findViewById(R.id.button_login);
        Button buttonBack = findViewById(R.id.id_button_ok_voltar);
        TextView buttonSignUp = findViewById(R.id.sing_up);
    }

    public void buttonLogar(View v) {

        TextView textLogin = findViewById(R.id.id_login);
        TextView textPassword = findViewById(R.id.id_password);

        String login = textLogin.getText().toString();
        String password = textPassword.getText().toString();

        if(login.equals("daniel") && password.equals("123")){
            alert("Login realizado com sucesso!\n");
            setContentView(R.layout.activity_user_area);
        }else {
            textLogin.setText("");
            textPassword.setText("");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Login Failed")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();
        }
    }


    public void buttonSignUp (View View) { setContentView(R.layout.activity_singup__form);}

    public void buttonBack (View View){
        setContentView(R.layout.activity_main);
    }


    private void alert(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}


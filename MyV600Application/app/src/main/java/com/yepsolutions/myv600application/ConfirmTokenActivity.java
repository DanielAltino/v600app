package com.yepsolutions.myv600application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmTokenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_token);

        final EditText tvTokenValue = (EditText) findViewById(R.id.id_etTokenValue);
        final Button btnConfirmToken = (Button) findViewById(R.id.id_buttonConfirmToken);

        btnConfirmToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tokenValue = tvTokenValue.getText().toString();
                GlobalClass globalClass = (GlobalClass) getApplicationContext();
                String email = globalClass.getEmail();

                if(!tokenValue.equals("")) {
                    Toast.makeText(ConfirmTokenActivity.this, "Funcionando TOKEN valor: " + tokenValue, Toast.LENGTH_SHORT).show();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
                                    Toast.makeText(ConfirmTokenActivity.this, "Your token is valid!", Toast.LENGTH_SHORT).show();
                                    Intent createNewPassword = new Intent(ConfirmTokenActivity.this, ResetPasswordActivity.class);
                                    startActivity(createNewPassword);
                                } else {
                                    Toast.makeText(ConfirmTokenActivity.this, "Check your token!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(ConfirmTokenActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    };
                    ConfirmTokenRequest confirmTokenRequest = new ConfirmTokenRequest(email, tokenValue, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ConfirmTokenActivity.this);
                    queue.add(confirmTokenRequest);
                }
                else{
                    Toast.makeText(ConfirmTokenActivity.this, "Token can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

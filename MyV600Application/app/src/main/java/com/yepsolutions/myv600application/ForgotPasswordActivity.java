package com.yepsolutions.myv600application;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        final EditText emailPassword = (EditText) findViewById(R.id.etEmailPassword);
        final Button resetPassword = (Button) findViewById(R.id.btnResetPassword);

        Toast.makeText(ForgotPasswordActivity.this, "WORKING", Toast.LENGTH_SHORT).show();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            Toast.makeText(ForgotPasswordActivity.this, "Valor: " + success, Toast.LENGTH_SHORT).show();
                            if(success){
                                Toast.makeText(ForgotPasswordActivity.this, "Email sent!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                builder.setMessage("Forgot password failed, try again later!")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(ForgotPasswordActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                };

                ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(email, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
                queue.add(forgotPasswordRequest);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
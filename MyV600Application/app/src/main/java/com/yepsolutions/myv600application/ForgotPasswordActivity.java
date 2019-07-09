package com.yepsolutions.myv600application;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final EditText emailPassword = (EditText) findViewById(R.id.etEmailPassword);
        final Button resetPassword = (Button) findViewById(R.id.btnResetPassword);

        Toast.makeText(ForgotPasswordActivity.this, "WORKING", Toast.LENGTH_SHORT).show();

        final GlobalClass globalClass = (GlobalClass) getApplicationContext();

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
                                Toast.makeText(ForgotPasswordActivity.this, "Email sent! Email is: " + email, Toast.LENGTH_SHORT).show();
                                globalClass.setEmail(email);
                                Intent TokenPage = new Intent(ForgotPasswordActivity.this, ConfirmTokenActivity.class);
                                startActivity(TokenPage);
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
}

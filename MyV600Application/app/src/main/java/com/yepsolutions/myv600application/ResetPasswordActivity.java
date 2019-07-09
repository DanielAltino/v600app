package com.yepsolutions.myv600application;

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

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        final EditText etNewPassword = (EditText) findViewById(R.id.id_etNewPassword);
        final EditText etConfirmNewPassword = (EditText) findViewById(R.id.id_etConfirmNewPassword);
        final Button btnConfirmNewPassword = (Button) findViewById(R.id.id_buttonNewPassword);

        btnConfirmNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newPassword = etNewPassword.getText().toString();
                final String confirmNewPassword = etConfirmNewPassword.getText().toString();
                //String email = "dparedes@yepsolutions.com.br";
                GlobalClass globalClass = (GlobalClass) getApplicationContext();

                String email = globalClass.getEmail();

                Toast.makeText(ResetPasswordActivity.this, "Email is: " + email , Toast.LENGTH_SHORT).show();
                if((newPassword.equals(confirmNewPassword)) && (!newPassword.equals("")) ){
                    //Toast.makeText(ResetPasswordActivity.this, "Passwords matches", Toast.LENGTH_SHORT).show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if(success){
                                    Toast.makeText(ResetPasswordActivity.this, "The new password is set!", Toast.LENGTH_SHORT).show();
                                    Intent loginPage = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                    startActivity(loginPage);
                                }
                                else{
                                    Toast.makeText(ResetPasswordActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e){
                                Toast.makeText(ResetPasswordActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    };
                    ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(email, newPassword, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ResetPasswordActivity.this);
                    queue.add(resetPasswordRequest);
                }
                else{
                    if(newPassword.equals("") || confirmNewPassword.equals("")){
                        Toast.makeText(ResetPasswordActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ResetPasswordActivity.this, "Passwords doesn't matches", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}

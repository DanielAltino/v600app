package com.yepsolutions.myv600application;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ConfirmTokenRequest extends StringRequest {
    //private static final String FORGOT_PASSWORD_REQUEST_URL = "https://yepsolutionsv600application.000webhostapp.com/ConfirmPasswordToken.php";
    private static final String FORGOT_PASSWORD_REQUEST_URL = "https://forgotpasswordlearningforyep.000webhostapp.com/ConfirmPasswordToken.php";
    //private static final String FORGOT_PASSWORD_REQUEST_URL = "https://forgotpasswordlearningforyep.000webhostapp.com/ForgotPassword.php";
    private Map<String, String> params;

    public ConfirmTokenRequest(String email, String token, Response.Listener<String> listener){
        super(Request.Method.POST, FORGOT_PASSWORD_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("token", token);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

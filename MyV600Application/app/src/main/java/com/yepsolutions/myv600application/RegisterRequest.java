package com.yepsolutions.myv600application;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://yepsolutionsv600application.000webhostapp.com/Register.php";
    //private static final String REGISTER_REQUEST_URL = "http://localhost/yep_v600app/Register.php";
    private Map<String, String> params;
    
    public RegisterRequest(String name, String username, String password, String telephone, String email,  Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("password", password);
        params.put("telephone", telephone);
        params.put("email", email);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

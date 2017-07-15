package com.fretron.fleet;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fretron.fleet.login.UserSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SplashScreen extends AppCompatActivity {
    ProgressBar progressBar ;
    Boolean connectionCheck;
    private static final String TAG = "Splash";
    UserSessionManager session;
    String token ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_splash_screen);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        connectionCheck = networkInfo != null && networkInfo.isConnected();

        progressBar = (ProgressBar)findViewById(R.id.SplashScreenprogressBar);

        session = new UserSessionManager(getApplicationContext());
        final Boolean check = session.isUserLoggedIn();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    HashMap<String , String> user = session.getUserDetails();
                    token = user.get(UserSessionManager.TOKEN);

                    if (connectionCheck) {

                        if (token != null && token.length() != 0 && !token.isEmpty()) {
                           // makeJsonObjectRequest();
                            Intent intent = new Intent("com.fretron.fleet.dashboard.DashBoard");
                            intent.putExtra("Token",token);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent("com.fretron.fleet.dashboard.DashBoard");
                            intent.putExtra("Token",token);
                            startActivity(intent);
//                            Intent intent = new Intent("com.fretron.fleet.login.Phone_no");
//                            startActivity(intent);
                            //Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();

                        }
                    }

                    else
                        Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_SHORT).show();

            }
        }, 3000);

    }

    private void makeJsonObjectRequest() {
        String urlJsonObj2 = "http://35.189.189.215:8094/authorize";
        final String[] jsonResponse2 = new String[1];

        JsonObjectRequest jsonObjReq2 = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj2,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String validity;
                        try {
                            validity = response.getString("valid");
                            jsonResponse2[0] = "";
                            jsonResponse2[0] += "valid: " + validity + "\n\n";
                            Log.d(TAG, response.toString());
                            //Toast.makeText(getApplicationContext(),jsonResponse2[0],Toast.LENGTH_SHORT).show();
                            if (validity.equals("true")){
                                EditText editText = (EditText)findViewById(R.id.editText);
                                //String phoneNo = editText.getText().toString();
                                Intent intent = new Intent("com.fretron.fleet.dashboard.DashBoard");
                                intent.putExtra("Token",token);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent("com.fretron.fleet.login.Phone_no");
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Not Working we are in catch",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("authorization", "Bearer "+token);
                return headers;
            }

        };
        // Adding request to request queue
        VolleyMain.getInstance().addToRequestQueue(jsonObjReq2);

    }


}

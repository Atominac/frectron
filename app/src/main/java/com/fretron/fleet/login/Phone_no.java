package com.fretron.fleet.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fretron.fleet.R;
import com.fretron.fleet.SmsListener;
import com.fretron.fleet.SmsReceiver;
import com.fretron.fleet.VolleyMain;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Phone_no extends AppCompatActivity {
    ArrayAdapter<CharSequence> arrayAdapter;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    private UserLoginTask mAuthTask = null;
    private OTPcheck task = null ;
    private ProgressDialog pDialog;
    private static final String TAG = "Phone_no";
    String token;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no);
        linearLayout = (LinearLayout)findViewById(R.id.linear_layout_1);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.CountryCodes, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        session = new UserSessionManager(getApplicationContext());

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editText = (EditText)findViewById(R.id.editText);
                String num1 = editText.getText().toString();

                ViewFlipper viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
                viewFlipper.setInAnimation(Phone_no.this, R.anim.view_transition_in_left);
                viewFlipper.setOutAnimation(Phone_no.this, R.anim.view_transition_out_left);
                viewFlipper.showNext();

                /*
                if (!num1.equals("") && num1.length()==10){
                    mAuthTask = new UserLoginTask(num1);
                    mAuthTask.execute((String) null);
                }
                else
                    Toast.makeText(Phone_no.this,"Please enter a valid no.",Toast.LENGTH_SHORT).show();

                     */


            }
        });

        FloatingActionButton button2 = (FloatingActionButton) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editText2 = (EditText)findViewById(R.id.editText2);
                String num1 = editText2.getText().toString();

                Intent intent = new Intent("com.fretron.fleet.dashboard.DashBoard");
                startActivity(intent);

                /*
                if (!num1.equals("") && num1.length()==6){
                    makeJsonObjectRequest();
                }
                else
                    Toast.makeText(Phone_no.this,"Invalid OTP",Toast.LENGTH_SHORT).show();

                    */


            }
        });

//        FloatingActionButton button3 = (FloatingActionButton) findViewById(R.id.button3);
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ViewFlipper viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
//                viewFlipper.setInAnimation(Phone_no.this, R.anim.view_transition_in_right);
//                viewFlipper.setOutAnimation(Phone_no.this, R.anim.view_transition_out_right);
//                viewFlipper.showPrevious();
//
//            }
//        });

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                EditText editText =  (EditText)findViewById(R.id.editText2);
                String otp  = messageText.replaceAll("\\D+","");
                editText.setText(otp);
                //Toast.makeText(Phone_no.this,"Message: "+messageText,Toast.LENGTH_LONG).show();
            }
        });

    }

    private class UserLoginTask extends AsyncTask<String, Void, String> {
        private final String mPhone;

        UserLoginTask(String phone_no) {
            mPhone = phone_no;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String result="",line="";
            String finalJson = "";
            String login_url="http://35.189.162.187:7078/login?mobileNumber="+mPhone;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(login_url);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                result= EntityUtils.toString(entity);

            }
            catch (IOException e) {
                result="Error...........";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            linearLayout.setVisibility(View.GONE);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(String result) {
            mAuthTask = null;
            Toast.makeText(Phone_no.this,result,Toast.LENGTH_SHORT).show();
            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.GONE);
            ViewFlipper viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
            viewFlipper.setInAnimation(Phone_no.this, R.anim.view_transition_in_left);
            viewFlipper.setOutAnimation(Phone_no.this, R.anim.view_transition_out_left);
            viewFlipper.showNext();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private class OTPcheck extends AsyncTask<String, Void, String>{
       private final String otp;

       OTPcheck(String no) {
           otp = no;
       }

       @Override
       protected String doInBackground(String... params) {
           String result="",line="";
           String login_url="";

           try {

               URL url = new URL(login_url);
               HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
               httpURLConnection.setRequestMethod("POST");
               httpURLConnection.disconnect();


           } catch (IOException e) {
               e.printStackTrace();
           }

           return result;
       }

       @Override
       protected void onPreExecute() {
       }


       @Override
       protected void onPostExecute(String result) {
       }

       @Override
       protected void onCancelled() {
       }
   }

    private void showpDialog() {
        if (!pDialog.isShowing())
        pDialog.show();
        }

    private void hidepDialog() {
        if (pDialog.isShowing())
        pDialog.dismiss();
        }

    private void makeJsonObjectRequest() {
        showpDialog();
        EditText editText = (EditText)findViewById(R.id.editText);
        String phoneNo = editText.getText().toString();
        EditText editText2 = (EditText)findViewById(R.id.editText2);
        String otp = editText2.getText().toString();
        String urlJsonObj = "http://35.189.162.187:7078/authentication?mobileNumber="+ phoneNo + "&otp=" + otp;
        final String[] jsonResponse = new String[1];

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
                public void onResponse(JSONObject response) {
                try {
                        token = response.getString("token");
                        jsonResponse[0] = "";
                        jsonResponse[0] += "token: " + token + "\n\n";
                        //Toast.makeText(getApplicationContext(),jsonResponse[0],Toast.LENGTH_SHORT).show();
                        String show_token = jsonResponse[0];
                    // Authentication Process here
                        makeJsonObjectRequest2();
                }

                catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    hidepDialog();
                }

        }, new Response.ErrorListener() {

                    @Override
                        public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    hidepDialog();
                    }

        });

        VolleyMain.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void makeJsonObjectRequest2() {
        String urlJsonObj2 = "http://35.189.162.187:7078/authorize";
        final String[] jsonResponse2 = new String[1];

                    JsonObjectRequest jsonObjReq2 = new JsonObjectRequest(Request.Method.GET,
                            urlJsonObj2,null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    String validity = null;
                                    try {
                                        validity = response.getString("valid");
                                        jsonResponse2[0] = "";
                                        jsonResponse2[0] += "valid: " + validity + "\n\n";
                                        Log.d(TAG, response.toString());
                                        //Toast.makeText(getApplicationContext(),jsonResponse2[0],Toast.LENGTH_SHORT).show();
                                        if (validity.equals("true")){
                                            EditText editText = (EditText)findViewById(R.id.editText);
                                            String phoneNo = editText.getText().toString();
                                            session.create_login_session(phoneNo,token);
                                            Intent intent = new Intent("com.sample.project_frectron.DashBoard");
                                            intent.putExtra("Token",token);
                                            startActivity(intent);
                                        }
                                        else
                                            Toast.makeText(getApplicationContext(),"2nd reqest not working",Toast.LENGTH_SHORT).show();
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

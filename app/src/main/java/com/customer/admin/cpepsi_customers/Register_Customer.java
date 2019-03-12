package com.customer.admin.cpepsi_customers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class Register_Customer extends AppCompatActivity {
    CheckBox terms_view;
    Button sbmt;
    TextView tx_terms;
    EditText cus_name, cus_email, cus_num, cus_pass, cus_con;
    public static String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__customer);

        terms_view = (CheckBox) findViewById(R.id.terms_view);
        cus_name = findViewById(R.id.cus_name);
        cus_email = findViewById(R.id.cus_email);
        cus_num = findViewById(R.id.cus_num);
        cus_pass = findViewById(R.id.cus_pass);
        cus_con = findViewById(R.id.cus_con);
        sbmt = (Button) findViewById(R.id.sbmt);
        tx_terms = (TextView) findViewById(R.id.tx_terms);
        sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_The_Validation();

            }
        });
        terms_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warn_the_user();

            }
        });
    }

    private void Check_The_Validation() {

        if (cus_name.getText().toString().isEmpty()) {
            cus_name.setError("Name can not be empty");
            return;
        }

        if (cus_pass.getText().toString().isEmpty()) {
            cus_pass.setError("Password can not be empty");
            return;
        }
        if (cus_email.getText().toString().isEmpty()) {
            cus_email.setError("Email can not be empty");
            return;
        }
        if (!cus_name.getText().toString().isEmpty() && !cus_pass.getText().toString().isEmpty() && !cus_email.getText().toString().isEmpty()) {
            if (cus_con.getText().toString().isEmpty() && cus_num.getText().toString().isEmpty()) {
                cus_num.requestFocus();
                cus_con.setError("Conferm Password can not be empty");
                cus_num.setError("Specify Contact number or Mobile number");
//                if(cus_num.getText().toString().length() ==10){
//
//                }

                return;
            } else {

                if (terms_view.isChecked()) {
                    new Check_Log_Infor_Cus(cus_name.getText().toString(), cus_email.getText().toString(), cus_pass.getText().toString(),
                            cus_con.getText().toString(), cus_con.getText().toString()).execute();
                } else {
                    Toast.makeText(Register_Customer.this, "Please tick on Agree terms and condition of ours or fill all fields", Toast.LENGTH_SHORT).show();
                }


            }

        } else {

            Toast.makeText(Register_Customer.this, "Please tick on Agree terms and condition of ours or fill all fields", Toast.LENGTH_SHORT).show();
            tx_terms.setTextColor(Color.RED);
        }
    }


    private void warn_the_user() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("We are going to use some features from your phone please grant CPEPSI");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        check_requested_permission();
                        Toast.makeText(Register_Customer.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void check_requested_permission() {
        ActivityCompat.requestPermissions(Register_Customer.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission_group.CONTACTS, Manifest.permission.CAMERA},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thanks", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Register_Customer.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class Check_Log_Infor_Cus extends AsyncTask<String, Void, String> {
        ProgressDialog Log_in_Progress;
        String email, password, contact_no, mobile_no, name;


        public Check_Log_Infor_Cus(String name, String email, String password, String contact_no, String mobile_no) {
            this.email = email;
            this.password = password;
            this.contact_no = contact_no;
            this.mobile_no = mobile_no;
            this.name = name;


        }

        @Override
        protected void onPreExecute() {
            Log_in_Progress = new ProgressDialog(Register_Customer.this);
            Log_in_Progress.show();
            Log_in_Progress.setCancelable(false);
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {
            try {

                URL url = new URL("https://www.paramgoa.com/cpepsi/api/Cust_Ragistration");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", email);
                postDataParams.put("password", password);
                postDataParams.put("contact", contact_no);
               // postDataParams.put("mobile", mobile_no);
                postDataParams.put("name", name);

                Log.e("postDataParams", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds*/);
                conn.setConnectTimeout(15000  /*milliseconds*/);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        result.append(line);
                    }
                    r.close();
                    return result.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }


        @Override
        protected void onPostExecute(String result) {

            Log_in_Progress.dismiss();

            Log.e("SendJsonDataToServer>>>", result.toString());
            try {

                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("massage");
                res = jsonObject.getString("responce");



                if (msg.equals("OTP Sent Successfully")) {
                    Intent to_completion = new Intent(Register_Customer.this, OtpActivity.class);
                    to_completion.putExtra("otp",res);
                    to_completion.putExtra("email", email);
                    to_completion.putExtra("password", password);
                    to_completion.putExtra("contact", contact_no);
                    to_completion.putExtra("name", name);

                    startActivity(to_completion);
                    Toast.makeText(Register_Customer.this, ""+msg, Toast.LENGTH_SHORT).show();
                    finish();
                } else {

//                    Snackbar.make(snac_v,""+response, Toast.LENGTH_LONG).show();
                    Toast.makeText(Register_Customer.this, "Otp not sent", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);


            super.onPostExecute(result);
        }
    }
}

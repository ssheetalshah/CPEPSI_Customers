package com.customer.admin.cpepsi_customers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.customer.admin.cpepsi_customers.util.AppPreference;

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

import instamojo.library.InstapayListener;
import instamojo.library.InstamojoPay;
import instamojo.library.Config;

import javax.net.ssl.HttpsURLConnection;


public class FeedbackForm extends AppCompatActivity {
    EditText feedName, feedEmail, feedContact, feedAddress, feedPayment;
    String FeedName, FeedEmail, FeedContact, FeedAddress, FeedPaymentAmount;
    RadioGroup radio_grop;
    RadioButton good, average, bad;
    Button feedSubmit;
    String Type;
    String Id;
    String Prov_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);

        feedName = (EditText) findViewById(R.id.feedName);
        feedEmail = (EditText) findViewById(R.id.feedEmail);
        feedContact = (EditText) findViewById(R.id.feedContact);
        feedAddress = (EditText) findViewById(R.id.feedAddress);
        feedPayment = (EditText) findViewById(R.id.feedPayment);
        feedSubmit = (Button) findViewById(R.id.feedSubmit);
        radio_grop = (RadioGroup) findViewById(R.id.radio_grop);
        good = (RadioButton) findViewById(R.id.good);
        average = (RadioButton) findViewById(R.id.average);
        bad = (RadioButton) findViewById(R.id.bad);

        FeedName = AppPreference.getName(this);
        FeedEmail = AppPreference.getEmail(this);
        FeedContact = AppPreference.getContact(this);

        feedName.setText(FeedName);
        feedEmail.setText(FeedEmail);
        feedContact.setText(FeedContact);

        //  good.setChecked(true);

        Prov_id = getIntent().getExtras().getString("PrID");

        feedSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedName = feedName.getText().toString();
                FeedEmail = feedEmail.getText().toString();
                FeedContact = feedContact.getText().toString();
                FeedAddress = feedAddress.getText().toString();
                FeedPaymentAmount = feedPayment.getText().toString();


                if (Connectivity.isNetworkAvailable(FeedbackForm.this)) {

                    if (FeedAddress.isEmpty()) {
                        feedAddress.setError("Address can not be empty");
                    }
                    if (FeedAddress.isEmpty()) {
                        feedAddress.setError("Address can not be empty");
                    } else if (!FeedAddress.isEmpty()) {

                        new PostFeedback().execute();
                    }

                } else {
                    Toast.makeText(FeedbackForm.this, "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        radio_grop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (good.isChecked()) {
                    Type = "good";
                    Id = "1";
                    average.setChecked(false);
                    bad.setChecked(false);
                    Toast.makeText(FeedbackForm.this, "type" + good, Toast.LENGTH_SHORT).show();
                }

                if (average.isChecked()) {
                    Type = "average";
                    Id = "2";
                    good.setChecked(false);
                    bad.setChecked(false);
                }

                if (bad.isChecked()) {
                    Type = "bad";
                    Id = "3";
                    average.setChecked(false);
                    good.setChecked(false);
                }
            }
        });
    }

    //---------------------------------------------

    public class PostFeedback extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(FeedbackForm.this);
            dialog.show();

        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("https://www.paramgoa.com/cpepsi/api/Add_feedback");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("customer_id", AppPreference.getId(FeedbackForm.this));
                postDataParams.put("name", FeedName);
                postDataParams.put("email", FeedEmail);
                postDataParams.put("contactno", FeedContact);
                postDataParams.put("address", FeedAddress);
                postDataParams.put("provider_id", Prov_id);
                postDataParams.put("service", Type);

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

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        StringBuffer Ss = sb.append(line);
                        Log.e("Ss", Ss.toString());
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                dialog.dismiss();

                JSONObject jsonObject = null;
                String s = result.toString();
                try {
                    jsonObject = new JSONObject(result);
                    String data = jsonObject.getString("data");
                    String responce = jsonObject.getString("responce");

                    if (responce.equals("true")) {
                        feedName.setText("");
                        feedEmail.setText("");
                        feedContact.setText("");
                        feedAddress.setText("");
                        //  Intent intent = new Intent(FeedbackForm.this, Main_Provider.class);
                        // startActivity(intent);
                        //finish();
                        callInstamojoPay(FeedEmail, FeedContact, FeedPaymentAmount, "PayServiceCharge", FeedName);

                    } else {
                        Toast.makeText(FeedbackForm.this, "Some Problem", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
    }


    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    InstapayListener listener;


    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                new InsertPaymentData().execute();
                Intent intent = new Intent(FeedbackForm.this, Main_Provider.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }


    public class InsertPaymentData extends AsyncTask<String, Void, String> {

//        ProgressDialog dialog;

        protected void onPreExecute() {
//            dialog = new ProgressDialog(FeedbackForm.this);
//            dialog.show();

        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://paramgoa.com/cpepsi/Api/insert_payment");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("user_id", AppPreference.getId(FeedbackForm.this));
                postDataParams.put("amount", FeedPaymentAmount);
                postDataParams.put("remark", AppPreference.getProblem(FeedbackForm.this));

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

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        StringBuffer Ss = sb.append(line);
                        Log.e("Ss", Ss.toString());
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                //  dialog.dismiss();

                JSONObject jsonObject = null;
                Log.e("PostPIinsuranceDetails", result.toString());
                try {

                    jsonObject = new JSONObject(result);
                    String res = jsonObject.getString("responce");

                    if (res.equals("true")) {

                        Toast.makeText(FeedbackForm.this, "Service Payment Success, Thanks for using our service!",
                                Toast.LENGTH_LONG).show();


                    } else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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

}

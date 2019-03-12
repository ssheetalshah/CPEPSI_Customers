package com.customer.admin.cpepsi_customers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OTP_Activity extends AppCompatActivity {
    Button ver_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_);
        ver_otp = findViewById(R.id.ver_otp);
        ver_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTP_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

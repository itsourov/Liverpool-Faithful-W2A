package com.liverpoolfaithful.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationProcess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(NotificationProcess.this, PostDetails.class);
        intent.putExtras(getIntent().getExtras());

        startActivity(intent);
        finish();

    }
}
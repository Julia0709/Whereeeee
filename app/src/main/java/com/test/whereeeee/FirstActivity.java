package com.test.whereeeee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends AppCompatActivity {

    private Button fSendLocation;
    private Button fGetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // sendLocationBtn
        fSendLocation =  (Button) findViewById(R.id.sendLocationBtn);
        fSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

        // getLocationBtn
        fGetLocation =  (Button) findViewById(R.id.getLocationBtn);
        fGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

    }

    // go to MyCurrentLocationActivity
    private void goToMap() {
        Intent goToMap = new Intent(getApplication(), MyCurrentLocationActivity.class);
        startActivity(goToMap);
    }

}

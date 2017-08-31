package com.example.gleb1.funshine.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.gleb1.funshine.R;

import static com.example.gleb1.funshine.activities.WeatherActivity.APP_PREFERENCES_COUNTER;
import static com.example.gleb1.funshine.activities.WeatherActivity.mSettings;


public class SettingsActivity extends AppCompatActivity {
    private ImageView imgFavorite;
    private TextView farSetting;
    private TextView celsiusSetting;
    private int Setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgFavorite = (ImageView) findViewById(R.id.backFromSettingsBtn);
        farSetting = (TextView) findViewById(R.id.farSetting);
        celsiusSetting = (TextView) findViewById(R.id.celsiusSetting);

        imgFavorite.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        farSetting.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                farSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                celsiusSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(APP_PREFERENCES_COUNTER, 1);
                editor.apply();
            }
        });

        celsiusSetting.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                celsiusSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                farSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(APP_PREFERENCES_COUNTER, 0);
                editor.apply();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSettings.contains(APP_PREFERENCES_COUNTER)) {
            // Получаем число из настроек
            Setting = mSettings.getInt(APP_PREFERENCES_COUNTER, 0);
            switch (Setting) {
                case 0:
                    celsiusSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                    farSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                    break;
                case 1:
                    farSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                    celsiusSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        WeatherActivity activity = new WeatherActivity();

    }
}

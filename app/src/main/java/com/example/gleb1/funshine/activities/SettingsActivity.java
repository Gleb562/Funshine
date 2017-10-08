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
import static com.example.gleb1.funshine.activities.WeatherActivity.APP_PREFERENCES_NOTIFICATION;
import static com.example.gleb1.funshine.activities.WeatherActivity.APP_PREFERENCES_PRESSURE;
import static com.example.gleb1.funshine.activities.WeatherActivity.mSettings;


public class SettingsActivity extends AppCompatActivity {
    private ImageView imgFavorite;
    private TextView farSetting;
    private TextView celsiusSetting;
    private TextView hpaSetting;
    private TextView mmHgSetting;
    private int Setting;
    private Switch notificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgFavorite = (ImageView) findViewById(R.id.backFromSettingsBtn);
        farSetting = (TextView) findViewById(R.id.farSetting);
        celsiusSetting = (TextView) findViewById(R.id.celsiusSetting);
        hpaSetting = (TextView) findViewById(R.id.hpaSetting);
        mmHgSetting = (TextView) findViewById(R.id.mmHgSetting);
        notificationSwitch = (Switch)findViewById(R.id.notificationSwitch);
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
                celsiusSetting.setTextColor(0xff7c7c7c);
                farSetting.setTextColor(0xff000000);
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
                farSetting.setTextColor(0xff7c7c7c);
                celsiusSetting.setTextColor(0xff000000);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(APP_PREFERENCES_COUNTER, 0);
                editor.apply();
            }
        });

        hpaSetting.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hpaSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                mmHgSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                mmHgSetting.setTextColor(0xff7c7c7c);
                hpaSetting.setTextColor(0xff000000);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(APP_PREFERENCES_PRESSURE, 1);
                editor.apply();
            }
        });

        mmHgSetting.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mmHgSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                hpaSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                hpaSetting.setTextColor(0xff7c7c7c);
                mmHgSetting.setTextColor(0xff000000);
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(APP_PREFERENCES_PRESSURE, 0);
                editor.apply();
            }
        });

        notificationSwitch.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mSettings.edit();
                if (notificationSwitch.isChecked() == false){
                    editor.putInt(APP_PREFERENCES_NOTIFICATION, 1);
                }else if(notificationSwitch.isChecked() == true){
                    editor.putInt(APP_PREFERENCES_NOTIFICATION, 0);
                }
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
                    farSetting.setTextColor(0xff7c7c7c);
                    celsiusSetting.setTextColor(0xff000000);
                    break;
                case 1:
                    farSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                    celsiusSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                    celsiusSetting.setTextColor(0xff7c7c7c);
                    farSetting.setTextColor(0xff000000);
                    break;
            }
        }
        if (mSettings.contains(APP_PREFERENCES_PRESSURE)) {
            // Получаем число из настроек
            Setting = mSettings.getInt(APP_PREFERENCES_PRESSURE, 0);
            switch (Setting) {
                case 1:
                    hpaSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                    mmHgSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                    mmHgSetting.setTextColor(0xff7c7c7c);
                    hpaSetting.setTextColor(0xff000000);
                    break;
                case 0:
                    mmHgSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners));
                    hpaSetting.setBackground(ContextCompat.getDrawable(getBaseContext(),R.drawable.corners_for_off_btn));
                    hpaSetting.setTextColor(0xff7c7c7c);
                    mmHgSetting.setTextColor(0xff000000);
                    break;
            }
        }

        if (mSettings.contains(APP_PREFERENCES_NOTIFICATION)) {
            Setting = mSettings.getInt(APP_PREFERENCES_NOTIFICATION, 0);
            switch (Setting) {
                case 0:
                    notificationSwitch.setChecked(true);
                    break;
                case 1:
                    notificationSwitch.setChecked(false);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

}

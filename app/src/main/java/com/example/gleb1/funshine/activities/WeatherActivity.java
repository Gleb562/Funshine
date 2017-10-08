package com.example.gleb1.funshine.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gleb1.funshine.services.MyService;
import com.example.gleb1.funshine.R;
import com.example.gleb1.funshine.model.DailyWeatherReport;
import com.example.gleb1.funshine.model.TodayWeatherReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import xyz.matteobattilana.library.WeatherView;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, SwipeRefreshLayout.OnRefreshListener,NavigationView.OnNavigationItemSelectedListener,DrawerLayout.DrawerListener {
    final String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast";
    final String URL_CURRENT_TEMP_BASE ="http://api.openweathermap.org/data/2.5/weather";
    final String URL_COORD = "/?lat=";//9.9687&lon=76.299";
    String URL_UNITS = "&units=metric";
    final String URL_API_KEY = "&APPID=55e01fcb06362d7a4d1ac9faf0b7ff76";


    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_COUNTER = "counter";
    public static final String APP_PREFERENCES_PRESSURE = "pressure";
    public static final String APP_PREFERENCES_NOTIFICATION = "notification";
    public static final String APP_PREFERENCES_LOCATION = "location";
    public static SharedPreferences mSettings;
    private int mCounter;

    private GoogleApiClient mGoogleApiClient;
    final int PERMISSION_LOCATION = 111;
    ArrayList<DailyWeatherReport> weatherReportlist = new ArrayList<>();
    ArrayList<TodayWeatherReport> todayWeatherReportlist = new ArrayList<>();
    //ArrayList<HourlyWeatherReport> hourlyWeatherReport = new ArrayList<>();

    private ImageView weatherIcon;
    private ImageView arrowDown;
    private ImageView umbrellaLogo;
    private TextView weatherDate;
    private TextView currentTemp;
    private TextView lowTemp;
    private TextView cityCountry;
    private TextView weatherDescription;
    private TextView updateTextView;
    private LinearLayout hourlyWeather;
    private TextView degreesTextView;
    private String degrees = " °C";
    private String windSpeedUnits = " m/s";
    private LinearLayout settingsBtn;
    private TextView currentNavLocatTemp;
    private Switch notificationSwitch;
    private CardView mainCardView;

    WeatherAdapter mAdapter;
    WeatherView weatherView;

    SwipeRefreshLayout swipeLayout;
    static DrawerLayout drawer;

    public Location refreshLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        weatherIcon = (ImageView)findViewById(R.id.weatherIcon);
        umbrellaLogo = (ImageView)findViewById(R.id.umbrellaLogo);
        weatherDate = (TextView)findViewById(R.id.weatherDate);
        currentTemp = (TextView)findViewById(R.id.currentTemp);
        lowTemp = (TextView)findViewById(R.id.lowTemp);
        cityCountry = (TextView)findViewById(R.id.cityCountry);
        weatherDescription = (TextView)findViewById(R.id.weatherDescription);
        updateTextView = (TextView)findViewById(R.id.updateTextView);
        degreesTextView = (TextView)findViewById(R.id.degreesTextView);
        settingsBtn = (LinearLayout) findViewById(R.id.settingsBtn);
        currentNavLocatTemp = (TextView)findViewById(R.id.currentNavLocatTemp);
        notificationSwitch = (Switch)findViewById(R.id.notificationSwitch);
        mainCardView = (CardView)findViewById(R.id.mainCardView);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_weather_reports);
        mAdapter = new WeatherAdapter(weatherReportlist);
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addConnectionCallbacks(this).addApi(LocationServices.API).addOnConnectionFailedListener(this).build();


        weatherView = (WeatherView)findViewById(R.id.weatherViewMain);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        settingsBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
            }
        });

        umbrellaLogo.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mSettings.contains(APP_PREFERENCES_NOTIFICATION)) {
            mCounter = mSettings.getInt(APP_PREFERENCES_NOTIFICATION, 0);
            if(mCounter == 0){
                if (!isMyServiceRunning()){
                    Intent serviceIntent = new Intent(getBaseContext(),MyService.class);
                    getBaseContext().startService(serviceIntent);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSettings.contains(APP_PREFERENCES_NOTIFICATION)) {
            mCounter = mSettings.getInt(APP_PREFERENCES_NOTIFICATION, 0);
            if(mCounter == 0){
                if (!isMyServiceRunning()){
                    Intent serviceIntent = new Intent(getBaseContext(),MyService.class);
                    getBaseContext().startService(serviceIntent);
                }
            }
        }
        /*// Запоминаем данные
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_COUNTER, mCounter);
        editor.apply();*/
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isMyServiceRunning()){
            Intent serviceIntent = new Intent(getBaseContext(),MyService.class);
            getBaseContext().stopService(serviceIntent);
        }
        if (mSettings.contains(APP_PREFERENCES_COUNTER)) {
            mCounter = mSettings.getInt(APP_PREFERENCES_COUNTER, 0);
            switch (mCounter){
                case 0:
                    URL_UNITS = "&units=imperial";
                    Log.v("Setting","фаренгейты");
                    degreesTextView.setText(" °F");
                    degrees = " °F";
                    windSpeedUnits = " m/h";
                    break;
                case 1:
                    URL_UNITS = "&units=metric";
                    Log.v("Setting","цельсии");
                    degreesTextView.setText(" °C");
                    degrees = " °C";
                    windSpeedUnits = " m/s";
                    break;
            }
        }
    }
    public void downloadWeatherData(Location location){
        final String url = URL_BASE + URL_COORD + location.getLatitude() + "&lon=" + location.getLongitude() + URL_UNITS + URL_API_KEY;
        final String urlForCurrTemp = URL_CURRENT_TEMP_BASE + URL_COORD + location.getLatitude() + "&lon=" + location.getLongitude() + URL_UNITS + URL_API_KEY;
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject city = response.getJSONObject("city");
                    String cityName = city.getString("name");
                    String country = city.getString("country");
                    JSONArray list = response.getJSONArray("list");
                    weatherReportlist.clear();

                    for(int i = 0; i < 40; i++){
                        JSONObject obj = list.getJSONObject(i);
                        JSONObject main = obj.getJSONObject("main");
                        String rawDate = obj.getString("dt_txt");
                        String rawtime = rawDate.substring(11,19);


                        if(rawDate.substring(11,19).equals("15:00:00")) {
                            double currentTemp = main.getDouble("temp");
                            double maxTemp = main.getDouble("temp_max");
                            double minTemp = main.getDouble("temp_min");
                            double humidity = main.getDouble("humidity");
                            double pressure = main.getDouble("pressure");
                            JSONArray weatherArray = obj.getJSONArray("weather");
                            JSONObject weather = weatherArray.getJSONObject(0);
                            JSONObject objWind = obj.getJSONObject("wind");
                            double windSpeed = objWind.getDouble("speed");
                            String weatherType = weather.getString("main");
                            JSONObject objClouds = obj.getJSONObject("clouds");
                            int clouds = objClouds.getInt("all");

                            DailyWeatherReport report = new DailyWeatherReport(cityName, country, (int) currentTemp, (int) maxTemp, (int) minTemp, weatherType, rawDate,(int)pressure,(int)humidity,(int) windSpeed, clouds);
                            weatherReportlist.add(report);
                        }

                        /*switch (rawtime){
                            case "00:00:00":
                                double temp0 = main.getDouble("temp");
                            case "03:00:00":
                                double temp3 = main.getDouble("temp");
                            case "06+:00:00":
                                double temp6 = main.getDouble("temp");
                            case "09:00:00":
                                double temp9 = main.getDouble("temp");
                            case "12:00:00":
                                double temp12 = main.getDouble("temp");
                            case "15:00:00":
                                double temp15 = main.getDouble("temp");
                            case "18:00:00":
                                double temp18 = main.getDouble("temp");
                            case "21:00:00":
                                double temp21 = main.getDouble("temp");
                        }
                        double hourlyTemp = main.getDouble("temp");
                        HourlyWeatherReport hourlyReport = new HourlyWeatherReport();
                        hourlyWeatherReport.add(hourlyReport);*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                updateUI();
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("FUN", "Err: " + error.getLocalizedMessage());
                Toast.makeText(WeatherActivity.this,"Cannot connect to the Internet...Please check your connection!", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(jsonRequest);



        final JsonObjectRequest jsonRequestForCurrWeather = new JsonObjectRequest(Request.Method.GET, urlForCurrTemp,null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                try {
                    todayWeatherReportlist.clear();
                    JSONObject temp = response.getJSONObject("main");
                    double currentTemperature = temp.getDouble("temp");
                    double todayTempMin = temp.getDouble("temp_min");
                    JSONArray todayWeatherArray = response.getJSONArray("weather");
                    JSONObject todayWeather = todayWeatherArray.getJSONObject(0);
                    String todayWeatherType = todayWeather.getString("main");
                        TodayWeatherReport TodayReport = new TodayWeatherReport((int)currentTemperature, (int)todayTempMin, todayWeatherType);
                            todayWeatherReportlist.add(TodayReport);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateUI();
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("FUN", "Err: " + error.getLocalizedMessage());
            }
        });
        Volley.newRequestQueue(this).add(jsonRequestForCurrWeather);

    }



    public void updateUI(){
        if (todayWeatherReportlist.size() > 0 && weatherReportlist.size() > 0){
            DailyWeatherReport report = weatherReportlist.get(0);
            TodayWeatherReport todayReport = todayWeatherReportlist.get(0);

            switch (todayReport.getTodayWeatherType()){
                case TodayWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    //weatherView.setWeather(Constants.weatherStatus.SUN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
                    break;
                case TodayWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    //weatherView.setWeather(Constants.weatherStatus.RAIN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
                    break;
                case TodayWeatherReport.WEATHER_TYPE_SNOW:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    //weatherView.setWeather(Constants.weatherStatus.SUN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
                    break;
                case TodayWeatherReport.WEATHER_TYPE_THUNDERSTORM:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning));
                    //weatherView.setWeather(Constants.weatherStatus.RAIN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
                default:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    //weatherView.setWeather(Constants.weatherStatus.SUN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (mSettings.contains(APP_PREFERENCES_NOTIFICATION)) {
                mCounter = mSettings.getInt(APP_PREFERENCES_NOTIFICATION, 0);
                switch (mCounter){
                    case 0:
                        Intent intent = new Intent(this, WeatherActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.umbrella_logo)
                                        .setContentTitle(Integer.toString(todayReport.getCurrentTemperature()) + degrees)
                                        .setContentText(report.getCityName() + ", " + report.getCountry())
                                        .setContentIntent(pIntent)
                                        .setOngoing(true);

                        notificationManager.notify(1,mBuilder.build());
                        break;
                    case 1:
                        notificationManager.cancel(1);
                        break;
                }
            }

            getCurrentDate();
            currentTemp.setText(Integer.toString(todayReport.getCurrentTemperature()));
            lowTemp.setText(Integer.toString(todayReport.getTodayTempMin()) + "\u00B0");
            cityCountry.setText(report.getCityName() + ", " + report.getCountry());
            weatherDescription.setText(todayReport.getTodayWeatherType());
            currentNavLocatTemp.setText(Integer.toString(todayReport.getCurrentTemperature()) + degrees);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        downloadWeatherData(location);
        refreshLocation = location;
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_LOCATION, URL_CURRENT_TEMP_BASE + URL_COORD + location.getLatitude() + "&lon=" + location.getLongitude() + URL_UNITS + URL_API_KEY);
        editor.apply();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_LOCATION);
            Log.v("DONKEY","Requesting permissions");
        }else {
            startLocationServices();
        }
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onConnectionSuspended(int i) {

    }



    public void startLocationServices(){
        Log.v("DONKEY","Starting Location Services Called");
        try {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,req,this);
            Log.v("DONKEY", "Requesting location updates");
        } catch (SecurityException e){
            Log.v("DONKEY",e.toString());
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startLocationServices();
                    Log.v("DONKEY", "Permission granted - starting services");
                }else {
                    Log.v("DONKEY", "Permission not granted");
                    Toast.makeText(this,"I can't run your location",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (newState == DrawerLayout.STATE_DRAGGING) {
        }
    }


    public class WeatherAdapter extends RecyclerView.Adapter<WeatherReportViewHolder>{

        private ArrayList<DailyWeatherReport> mDailyWeatherReports;
        //private ArrayList<HourlyWeatherReport> mHourlyWeatherReport;

        public WeatherAdapter(ArrayList<DailyWeatherReport> mDailyWeatherReports) {
            this.mDailyWeatherReports = mDailyWeatherReports;
            //this.mHourlyWeatherReport = mHourlyWeatherReport;
        }

        @Override
        public WeatherReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather,parent,false);
            return new WeatherReportViewHolder(card);
        }

        @Override
        public void onBindViewHolder(final WeatherReportViewHolder holder, int position) {
            DailyWeatherReport report = mDailyWeatherReports.get(position);
            //HourlyWeatherReport hourlyReport = mHourlyWeatherReport.get(position);
            holder.updateUI(report);

            final WeatherReportViewHolder vHolder = holder;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    collapseExpandTextView(holder.itemView);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mDailyWeatherReports.size();
        }
    }
    void collapseExpandTextView(View view) {
        hourlyWeather = (LinearLayout) view.findViewById(R.id.hourly_weather);
        arrowDown = (ImageView)view.findViewById(R.id.arrow_down);
        if (hourlyWeather.getVisibility() == view.GONE) {
            // it's collapsed - expand it
            hourlyWeather.setVisibility(view.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(arrowDown, "rotation", 90f, 0f).setDuration(200);
            objectAnimator.start();

            ValueAnimator va = ValueAnimator.ofInt(0, 270).setDuration(200);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    hourlyWeather.getLayoutParams().height = value.intValue();
                    hourlyWeather.requestLayout();
                }
            });
            va.start();
        } else if (hourlyWeather.getVisibility() == view.INVISIBLE){
            hourlyWeather.setVisibility(view.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(arrowDown, "rotation", 90f, 0f).setDuration(200);
            objectAnimator.start();

            ValueAnimator va = ValueAnimator.ofInt(0, 270).setDuration(200);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    hourlyWeather.getLayoutParams().height = value.intValue();
                    hourlyWeather.requestLayout();
                }
            });
            va.start();
        }
        else {
            // it's expanded - collapse it
            ValueAnimator va = ValueAnimator.ofInt(270, 0).setDuration(200);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    hourlyWeather.getLayoutParams().height = value.intValue();
                    hourlyWeather.requestLayout();
                }
            });
            va.start();
            hourlyWeather.setVisibility(view.INVISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(arrowDown, "rotation", 0f, 90f).setDuration(200);
            objectAnimator.start();
        }





    }


    public class WeatherReportViewHolder extends RecyclerView.ViewHolder{

        private ImageView weatherIcon;
        private TextView weatherDate;
        private TextView weatherDescription;
        private TextView tempHigh;
        private TextView tempLow;
        private TextView humidityTextView;
        private TextView pressureTextView;
        private TextView windTextView;
        private TextView cloudsTextView;
       /* private TextView temp1;
        private TextView temp2;
        private TextView temp3;
        private TextView temp4;
        private TextView temp5;
        private TextView temp6;
        private TextView temp7;
        private TextView temp8;*/

        public WeatherReportViewHolder(View itemView) {
            super(itemView);
            //HourlyWeatherReport report = hourlyWeatherReport.get(0);
            weatherIcon = (ImageView)itemView.findViewById(R.id.list_weather_icon);
            weatherDate = (TextView)itemView.findViewById(R.id.list_weather_day);
            weatherDescription = (TextView)itemView.findViewById(R.id.list_weather_description);
            tempHigh = (TextView)itemView.findViewById(R.id.list_weather_temp_high);
            tempLow = (TextView)itemView.findViewById(R.id.list_weather_temp_low);
            humidityTextView = (TextView)itemView.findViewById(R.id.humidityTextView);
            pressureTextView = (TextView)itemView.findViewById(R.id.pressureTextView);
            windTextView = (TextView)itemView.findViewById(R.id.windTextView);
            cloudsTextView = (TextView)itemView.findViewById(R.id.cloudsTextView);
            /*temp1 = (TextView)itemView.findViewById(R.id.temp1);
            temp2 = (TextView)itemView.findViewById(R.id.temp2);
            temp3 = (TextView)itemView.findViewById(R.id.temp3);
            temp4 = (TextView)itemView.findViewById(R.id.temp4);
            temp5 = (TextView)itemView.findViewById(R.id.temp5);
            temp6 = (TextView)itemView.findViewById(R.id.temp6);
            temp7 = (TextView)itemView.findViewById(R.id.temp7);
            temp8 = (TextView)itemView.findViewById(R.id.temp8);*/
        }

        public void updateUI(DailyWeatherReport report){
            weatherDate.setText(report.getFormatted_list_date());
            weatherDescription.setText(report.getWeather());
            tempHigh.setText(Integer.toString(report.getMaxTemp()) + degrees);
            tempLow.setText(Integer.toString(report.getMinTemp()) + degrees);
            humidityTextView.setText((Integer.toString(report.getHumidity()))+ "%");
            pressureTextView.setText((Integer.toString(report.getPressure()))+ " hpa");
            windTextView.setText((Integer.toString(report.getWindSpeed()))+ windSpeedUnits);
            cloudsTextView.setText((Integer.toString(report.getClouds())) + " %");

            if (mSettings.contains(APP_PREFERENCES_PRESSURE)) {
                mCounter = mSettings.getInt(APP_PREFERENCES_PRESSURE, 0);
                switch (mCounter){
                    case 1:
                        pressureTextView.setText((Integer.toString(report.getPressure()))+ " hpa");
                        break;
                    case 0:
                        double tempPressure = report.getPressure() * 0.750064;
                        String formattedDouble = new DecimalFormat("#0.00").format(tempPressure);
                        pressureTextView.setText(formattedDouble + " mmHg");
                        break;
                }
            }

            switch (report.getWeather()){
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_THUNDERSTORM:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning_mini));
                default:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));
            }

        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
                /*finish();
                startActivity(getIntent());*/
                downloadWeatherData(refreshLocation);
            }
        }, 500);

    }

    public void refreshActivity(){

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addConnectionCallbacks(this).addApi(LocationServices.API).addOnConnectionFailedListener(this).build();
    }


    public void getCurrentDate(){
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);
        SimpleDateFormat sdfUpdateTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String dateString = sdf.format(date);
        String lastUpdateString = sdfUpdateTime.format(date);
        weatherDate.setText("Today, " + dateString);
        updateTextView.setText("Last update: " + lastUpdateString);
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current hour
        if(currentHour > 18 || currentHour < 6){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(0xFF7168b2);
                mainCardView.setBackgroundColor(0xFF7177b2);
            }
        }


        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim);
        findViewById(R.id.updateTextView).startAnimation(shake);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}

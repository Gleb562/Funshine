package com.example.gleb1.funshine;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gleb1.funshine.model.DailyWeatherReport;
import com.example.gleb1.funshine.model.TodayWeatherReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.example.gleb1.funshine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import xyz.matteobattilana.library.Common.Constants;
import xyz.matteobattilana.library.WeatherView;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, SwipeRefreshLayout.OnRefreshListener {
    final String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast";
    final String URL_COORD = "/?lat=";//9.9687&lon=76.299";
    final String URL_UNITS = "&units=metric";
    final String URL_API_KEY = "&APPID=55e01fcb06362d7a4d1ac9faf0b7ff76";

    final String URL_CURRENT_TEMP_BASE ="http://api.openweathermap.org/data/2.5/weather";

    private GoogleApiClient mGoogleApiClient;
    final int PERMISSION_LOCATION = 111;
    ArrayList<DailyWeatherReport> weatherReportlist = new ArrayList<>();
    ArrayList<TodayWeatherReport> todayWeatherReportlist = new ArrayList<>(0);

    private ImageView weatherIcon;
    private ImageView weatherIconMini;
    private TextView weatherDate;
    private TextView currentTemp;
    private TextView lowTemp;
    private TextView cityCountry;
    private TextView weatherDescription;
    private TextView updateTextView;

    WeatherAdapter mAdapter;
    WeatherView weatherView;

    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherIcon = (ImageView)findViewById(R.id.weatherIcon);
        weatherIconMini = (ImageView)findViewById(R.id.weatherIconMini);
        weatherDate = (TextView)findViewById(R.id.weatherDate);
        currentTemp = (TextView)findViewById(R.id.currentTemp);
        lowTemp = (TextView)findViewById(R.id.lowTemp);
        cityCountry = (TextView)findViewById(R.id.cityCountry);
        weatherDescription = (TextView)findViewById(R.id.weatherDescription);
        updateTextView = (TextView)findViewById(R.id.updateTextView);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_weather_reports);
        mAdapter = new WeatherAdapter(weatherReportlist);
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addConnectionCallbacks(this).addApi(LocationServices.API).addOnConnectionFailedListener(this).build();


        weatherView = (WeatherView)findViewById(R.id.weatherViewMain);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

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

                    /*JSONObject dateObj = list.getJSONObject(0);
                    String todayDate =dateObj.getString("dt_txt");*/

                    weatherReportlist.clear();

                    for(int i = 0; i < 40; i++){
                        JSONObject obj = list.getJSONObject(i);
                        JSONObject main = obj.getJSONObject("main");
                        String rawDate =obj.getString("dt_txt");
                        DailyWeatherReport report;

                        if(rawDate.substring(11,19).equals("15:00:00")) {
                            double currentTemp = main.getDouble("temp");
                            double maxTemp = main.getDouble("temp_max");
                            double minTemp = main.getDouble("temp_min");

                            JSONArray weatherArray = obj.getJSONArray("weather");
                            JSONObject weather = weatherArray.getJSONObject(0);
                            String weatherType = weather.getString("main");

                               /* for(int x = 0; x< 40; x++){
                                    JSONObject objMinTemp = list.getJSONObject(x);
                                    JSONObject mainMinTemp = objMinTemp.getJSONObject("main");
                                    String rawDateMinTemp =objMinTemp.getString("dt_txt");
                                    if(rawDateMinTemp.substring(11,19).equals("00:00:00")){
                                        minTemp = mainMinTemp.getDouble("temp_min");
                                        report = new DailyWeatherReport(cityName, country, (int) currentTemp, (int) maxTemp, (int) minTemp, weatherType, rawDate);
                                        weatherReportlist.add(report);
                                    }
                                }*/
                            report = new DailyWeatherReport(cityName, country, (int) currentTemp, (int) maxTemp, (int) minTemp, weatherType, rawDate);
                            weatherReportlist.add(report);
                        }

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
                    weatherView.setWeather(Constants.weatherStatus.SUN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
                    break;
                case TodayWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    weatherView.setWeather(Constants.weatherStatus.RAIN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
                    break;
                case TodayWeatherReport.WEATHER_TYPE_SNOW:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    weatherView.setWeather(Constants.weatherStatus.SUN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
                    break;
                case TodayWeatherReport.WEATHER_TYPE_THUNDERSTORM:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning));
                    weatherView.setWeather(Constants.weatherStatus.RAIN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
                default:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    //weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    weatherView.setWeather(Constants.weatherStatus.SUN).setLifeTime(1650).setFadeOutTime(1000).setParticles(43).setFPS(60).setAngle(-5).startAnimation();
            }

            //weatherDate.setText(report.getFormatted_date());
            getCurrentDate();
            currentTemp.setText(Integer.toString(todayReport.getCurrentTemperature()));
            lowTemp.setText(Integer.toString(todayReport.getTodayTempMin()) + "\u00B0");
            cityCountry.setText(report.getCityName() + ", " + report.getCountry());
            weatherDescription.setText(todayReport.getTodayWeatherType());
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        downloadWeatherData(location);
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



    public class WeatherAdapter extends RecyclerView.Adapter<WeatherReportViewHolder>{

        private ArrayList<DailyWeatherReport> mDailyWeatherReports;

        public WeatherAdapter(ArrayList<DailyWeatherReport> mDailyWeatherReports) {
            this.mDailyWeatherReports = mDailyWeatherReports;
        }

        @Override
        public WeatherReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather,parent,false);
            return new WeatherReportViewHolder(card);
        }

        @Override
        public void onBindViewHolder(WeatherReportViewHolder holder, int position) {
            DailyWeatherReport report = mDailyWeatherReports.get(position);
            holder.updateUI(report);
        }

        @Override
        public int getItemCount() {
            return mDailyWeatherReports.size();
        }
    }


    public class WeatherReportViewHolder extends RecyclerView.ViewHolder{

        private ImageView weatherIcon;
        private TextView weatherDate;
        private TextView weatherDescription;
        private TextView tempHigh;
        private TextView tempLow;

        public WeatherReportViewHolder(View itemView) {
            super(itemView);

            weatherIcon = (ImageView)itemView.findViewById(R.id.list_weather_icon);
            weatherDate = (TextView)itemView.findViewById(R.id.list_weather_day);
            weatherDescription = (TextView)itemView.findViewById(R.id.list_weather_description);
            tempHigh = (TextView)itemView.findViewById(R.id.list_weather_temp_high);
            tempLow = (TextView)itemView.findViewById(R.id.list_weather_temp_low);
        }

        public void updateUI(DailyWeatherReport report){

            weatherDate.setText(report.getFormatted_list_date());
            weatherDescription.setText(report.getWeather());
            tempHigh.setText(Integer.toString(report.getMaxTemp()) + " \u2103");
            tempLow.setText(Integer.toString(report.getMinTemp()) + " \u2103");

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
                finish();
                startActivity(getIntent());
            }
        }, 500);

    }
//csashtddccdcd
    public void getCurrentDate(){
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);
        SimpleDateFormat sdfUpdateTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String dateString = sdf.format(date);
        String lastUpdateString = sdfUpdateTime.format(date);
        weatherDate.setText("Today, " + dateString);
        updateTextView.setText("Last update: " + lastUpdateString);
    }

}

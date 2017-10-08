package com.example.gleb1.funshine.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gleb1.funshine.R;
import com.example.gleb1.funshine.activities.WeatherActivity;
import com.example.gleb1.funshine.model.TodayWeatherReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.gleb1.funshine.activities.WeatherActivity.APP_PREFERENCES_COUNTER;
import static com.example.gleb1.funshine.activities.WeatherActivity.APP_PREFERENCES_LOCATION;
import static com.example.gleb1.funshine.activities.WeatherActivity.mSettings;




public class MyService extends Service {

    private String urlForCurrTemp = mSettings.getString(APP_PREFERENCES_LOCATION, "defaultStringIfNothingFound");
    public String currentNotifTemp;
    public String currentNotifCity;
    public static final String APP_PREFERENCES_LO = "locations";
    private String degrees = " °C";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("BOBO","SERVISE IS RUNNING");
        Log.v("BOBO",urlForCurrTemp);
        Producer producer = new Producer();

        if (mSettings.contains(APP_PREFERENCES_COUNTER)) {
           int mCounter = mSettings.getInt(APP_PREFERENCES_COUNTER, 0);
            switch (mCounter){
                case 0:
                    degrees = " °F";
                    break;
                case 1:
                    degrees = " °C";
                    break;
            }
        }

        /*Thread myThready = new Thread(new Runnable()
        {
            public void run() //Этот метод будет выполняться в побочном потоке
            {
                notificationWeatherData(urlForCurrTemp);
            }
        });
        myThready.start();	//Запуск потока
        try {
            myThready.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        timer.schedule(hourlyTask, 0L, 1000*60*10);
        //startActivity(new Intent(getBaseContext(), WeatherActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        return super.onStartCommand(intent, flags, startId);
    }


    class Producer implements Runnable{
        public void run(){
            notificationWeatherData(urlForCurrTemp);
        }
    }

    public void notificationWeatherData(String urlForCurrTemp){
        final JsonObjectRequest jsonRequestForCurrWeather = new JsonObjectRequest(Request.Method.GET, urlForCurrTemp,null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject temp = response.getJSONObject("main");
                    JSONObject country = response.getJSONObject("sys");
                    currentNotifTemp = Double.toString(temp.getDouble("temp"));
                    currentNotifCity = response.getString("name") + ", " + country.getString("country");
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(APP_PREFERENCES_LO, currentNotifTemp);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateUI();
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
        String temperature = mSettings.getString(APP_PREFERENCES_LO, "defaultStringIfNothingFound");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, WeatherActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.umbrella_logo)
                        .setContentTitle(temperature + degrees)
                        .setContentText(currentNotifCity)
                        .setContentIntent(pIntent)
                        .setOngoing(true);

        notificationManager.notify(1,mBuilder.build());
    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        timer.cancel();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    Timer timer = new Timer();
    TimerTask hourlyTask = new TimerTask() {
        @Override
        public void run () {
            Message msg = new Message();
            msg.arg1=1;
            handler.sendMessage(msg);

        }
    };


    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.arg1==1)
            {
                notificationWeatherData(urlForCurrTemp);
                /*Toast toast = Toast.makeText(getApplicationContext(),
                        "Пора покормить кота!" + currentNotifTemp, Toast.LENGTH_SHORT);
                toast.show();*/
            }
            return false;
        }
    });



}

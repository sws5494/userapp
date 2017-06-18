package com.example.user.userapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.user.userapp.MainActivity.ja;

/**
 * Created by user on 2017-06-13.
 */

public class MyService extends Service {
    Handler myHaldler = null;
    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        myHaldler = new Handler();
        thread.start();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            /*Notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setSmallIcon(R.drawable.logo)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            Notifi.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            Notifi.flags = Notification.FLAG_AUTO_CANCEL;


            Notifi_M.notify( 777 , Notifi);*/


            Thread thread = new Thread() {
                public void run() {
                    try {
                        String gps = checkGPS();
                        String phonenum = phoneNum(MyService.this);

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat CurTimeFormat = new SimpleDateFormat("dd-HH-mm-ss");
                        String myTime = CurTimeFormat.format(date);

//                        myUpdate("http://192.168.64.166:3000/user_gps?phonenum=" + phonenum + "&gps=" + gps);
//                        myUpdate("http://192.168.64.166:3000/user_install?phonenum=" + phonenum + "&myTime=" + myTime);
                        select2("http://192.168.64.166:3000/data");
//                        MainActivity.adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.d("MyService", "gps changed error");
                    }
                }
            };
            thread.start();
//            Toast.makeText(MyService.this, "Service On", Toast.LENGTH_LONG).show();
        }
    }

    ;

    private void sendNotification(String text) {
        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Set the notification contents
        builder.setSmallIcon(com.sousoum.libgeofencehelper.R.drawable.default_notif)
                .setContentTitle("알림")
                .setTicker("외출내역을 확인하세요")
                .setVibrate(new long[]{0, 1000})
                .setContentText(text);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }


    int i = 0;
    String T_id = null;

    public void select2(String link) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while (true) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        sb.append(line + "\n");
                    }
                    br.close();
                }
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.d("ERROR select2", "ERROR select2");
        }

        String jsonString = sb.toString();
        try {
            MainActivity.ja = new JSONArray(jsonString);

            for (i = 0; i < MainActivity.ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                MainActivity.identifier2[i] = jo.getString("identifier");
                T_id = jo.getString("identifier");
                MainActivity.startday2[i] = jo.getString("startday");
                MainActivity.starttime2[i] = jo.getString("starttime");
                MainActivity.endday2[i] = jo.getString("endday");
                MainActivity.endtime2[i] = jo.getString("endtime");
                MainActivity.reason2[i] = jo.getString("reason");
                MainActivity.allow2[i] = jo.getString("allow");
                MainActivity.notify[i] = jo.getString("notify");
                if (MainActivity.notify[i].equals("update")) {
                    Log.d("NOTIFY", jo.getString("identifier"));
                    new Thread() {
                        public void run() {
                            myUpdate("http://192.168.64.166:3000/request_allow?&identifier=" + T_id + "&notify=" + "empty");
                            myHaldler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sendNotification("외출내역을 확인하세요");
                                }
                            });
                        }
                    }.start();
                }
            }
        } catch (JSONException e) {
            Log.d("ERROR", "ERROR");
        }
    }

    public String checkGPS() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPS) {
            return "ON";
        } else {
            Log.d("GPS", "CHECK");
        }
        return "OFF";
    }

    public String phoneNum(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String phoneNum = telManager.getLine1Number();
        if (phoneNum != null) {
            phoneNum = phoneNum.replaceFirst("\\+82", "0");
        }
        return phoneNum;
    }

    public void myUpdate(String link) {
        Log.d("UPDATE", "UPDATE");
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while (true) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        sb.append(line + "\n");
                    }
                    br.close();
                }
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.d("MyService", "gps changed error");
        }
    }

    public void myUpdate2(String link) {
        Log.d("UPDATE", "UPDATE");
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while (true) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        sb.append(line + "\n");
                    }
                    br.close();
                }
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.d("MyService", "gps changed error");
        }
    }
}
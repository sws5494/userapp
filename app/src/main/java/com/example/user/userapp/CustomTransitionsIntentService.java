package com.example.user.userapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.sousoum.libgeofencehelper.StorableGeofence;
import com.sousoum.libgeofencehelper.StorableGeofenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Djavan on 13/12/2014.
 */
public class CustomTransitionsIntentService extends IntentService {

    public CustomTransitionsIntentService() {
        super("CustomTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String notificationText = "Not a geo event";
        String transitionStr=null;
        GeofencingEvent geoEvent = GeofencingEvent.fromIntent(intent);
        if (geoEvent != null) {
            if (geoEvent.hasError()) {
                notificationText = "Error : " + geoEvent.getErrorCode();
            } else {
                int transition = geoEvent.getGeofenceTransition();
                switch (transition) {
                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                        transitionStr = "Enter";
                        break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        transitionStr = "Exit";
                        break;
                    case Geofence.GEOFENCE_TRANSITION_DWELL:
                        transitionStr = "Dwell";
                        break;
                    default:
                        transitionStr = "Unknown";
                }

                StorableGeofenceManager manager = new StorableGeofenceManager(this);

                List<Geofence> triggeringGeo = geoEvent.getTriggeringGeofences();
                Log.d("TAG", transitionStr);
                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append(transitionStr);
                for (int i = 0; i < triggeringGeo.size(); i++) {
                    Geofence geo = triggeringGeo.get(i);
                    StorableGeofence storableGeofence = manager.getGeofence(geo.getRequestId());
//                    strBuilder.append(geo.getRequestId());
                    if (storableGeofence != null && storableGeofence.getAdditionalData() != null) {
                        HashMap<String, Object> additionalData = storableGeofence.getAdditionalData();
//                        strBuilder.append(additionalData.get(MainActivity.ADDITIONAL_DATA_TIME));
                    }

//                    strBuilder.append("-");
                }
                notificationText = strBuilder.toString();
            }
        }

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("HH:mm:ss");
        String strNow = sdfNow.format(date);
        String strNow2 = sdfNow2.format(date);

        String identifier = GetDevicesUUID(CustomTransitionsIntentService.this);

        Log.d("TAGid", identifier);
        Log.d("TAGonoff", transitionStr);
        Log.d("TAGtime", ""+date);

        insert("http://192.168.64.166:3000/geofence?identifier="+identifier+"&"+"onoff="+transitionStr+"&"+"time="+strNow+"&"+"time2="+strNow2);
        sendNotification(notificationText);
    }

    private void sendNotification(String text) {
        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Set the notification contents
        builder.setSmallIcon(com.sousoum.libgeofencehelper.R.drawable.default_notif)
                .setContentTitle("Custom")
                .setContentText(text);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private String GetDevicesUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    public void insert(String link) {
        insertToDatabase(link);
    }

    private void insertToDatabase(String link) {

        class InsertData extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String link = (String)params[0];
                    Log.d("TAGlink", link);
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    /*while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    try {
                        JSONArray jsonArray = new JSONArray(line);
                        Log.d("TAG", "" + jsonArray.length());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

                    Log.d("TAG", sb.toString());
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        InsertData task = new InsertData();
        task.execute(link);
    }
}

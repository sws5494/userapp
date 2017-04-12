package com.example.user.userapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.sousoum.libgeofencehelper.StorableGeofence;
import com.sousoum.libgeofencehelper.StorableGeofenceManager;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity implements LocationListener, StorableGeofenceManager.StorableGeofenceManagerListener {
    LinearLayout boss_layout;

    TextView tab_request, tab_list;
    TextView tv_start_day, tv_start_time, tv_end_day, tv_end_time;

    Button btn_work, btn_eat, btn_etc, btn_request;

    DatePickerDialog date_dialog;
    TimePickerDialog time_dialog;

    int start_year, start_month, start_day, start_hour, start_minute;
    int end_year, end_month, end_day, end_hour, end_minute;
    int reason;

    boolean Flag_day, Flag_time;

    /*************************************************************************************************************/

    private static final String TAG = "MainActivity";
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 0;

    private static final String GEOFENCE_ID_FOR_DEFAULT_RECEIVER = "DefaultReceiverGeofence";
    private static final String GEOFENCE_ID_FOR_CUSTOM_RECEIVER = "CustomReceiverGeofence";

    public static final String ADDITIONAL_DATA_TIME = "Time";
    public static final String ADDITIONAL_DATA_PACKAGE = "Package";

    private LocationManager mLocationManager;
    private StorableGeofenceManager mGeofenceManager;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boss_layout = (LinearLayout) findViewById(R.id.boss_layout);

        tab_request = (TextView) findViewById(R.id.tab_request);
        tab_list = (TextView) findViewById(R.id.tab_list);

        tv_start_day = (TextView) findViewById(R.id.start_day);
        tv_start_time = (TextView) findViewById(R.id.start_time);
        tv_end_day = (TextView) findViewById(R.id.end_day);
        tv_end_time = (TextView) findViewById(R.id.end_time);

        btn_work = (Button) findViewById(R.id.btn_work);
        btn_eat = (Button) findViewById(R.id.btn_eat);
        btn_etc = (Button) findViewById(R.id.btn_etc);
        btn_request = (Button) findViewById(R.id.btn_request);

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        final SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH시 mm분");
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat CurMinuteFormat = new SimpleDateFormat("mm");

        String strCurDate = CurDateFormat.format(date);
        String strCurTime = CurTimeFormat.format(date);

        int strCurYear = Integer.parseInt(CurYearFormat.format(date));
        int strCurMonth = Integer.parseInt(CurMonthFormat.format(date));
        int strCurDay = Integer.parseInt(CurDayFormat.format(date));
        int strCurHour = Integer.parseInt(CurHourFormat.format(date));
        int strCurMinute = Integer.parseInt(CurMinuteFormat.format(date));

        tab_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab_request.setBackgroundColor(Color.rgb(242, 242, 242));
                tab_list.setBackgroundColor(Color.rgb(255, 255, 255));
                boss_layout.setVisibility(View.VISIBLE);
            }
        });

        tab_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab_list.setBackgroundColor(Color.rgb(242, 242, 242));
                tab_request.setBackgroundColor(Color.rgb(255, 255, 255));
                boss_layout.setVisibility(View.INVISIBLE);
            }
        });

        tv_start_day.setText(strCurDate);
        tv_start_time.setText(strCurTime);
        tv_end_day.setText(strCurDate);
        if (strCurHour + 3 < 24) {
            int real_strCurHour = strCurHour + 3;
            if (0 < strCurMinute && strCurMinute < 10) {
                tv_end_time.setText("" + real_strCurHour + "시 " + "0" + strCurMinute + "분");
            } else {
                tv_end_time.setText("" + real_strCurHour + "시 " + strCurMinute + "분");
            }
        } else {
            if (0 < strCurMinute && strCurMinute < 10) {
                tv_end_time.setText("" + strCurHour + "시 " + "0" + strCurMinute + "분");
            } else {
                tv_end_time.setText("" + strCurHour + "시 " + strCurMinute + "분");
            }
        }

        date_dialog = new DatePickerDialog(this, date_listener, strCurYear, strCurMonth, strCurDay);
        time_dialog = new TimePickerDialog(this, time_listener, strCurHour, strCurMinute, false);

        tv_start_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_day = true;
                date_dialog.show();
            }
        });

        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_time = true;
                time_dialog.show();
            }
        });

        tv_end_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_day = false;
                date_dialog.show();
            }
        });

        tv_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_time = false;
                time_dialog.show();
            }
        });

        btn_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = 1;
                btn_eat.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_work.setBackgroundColor(Color.rgb(217, 217, 217));
            }
        });

        btn_eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = 2;
                btn_work.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_eat.setBackgroundColor(Color.rgb(217, 217, 217));
            }
        });

        btn_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = 3;
                btn_work.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_eat.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(217, 217, 217));
            }
        });

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reason == 0) {
                    Toast.makeText(getApplicationContext(), "외출사유를 선택하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "시작: " + start_year + "/" + start_month + "/" + start_day + "/" + start_hour + "/" + start_minute, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "종료: " + end_year + "/" + end_month + "/" + end_day + "/" + end_hour + "/" + end_minute, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "사유: " + reason, Toast.LENGTH_SHORT).show();
            }
        });

        /*****************************************************************************************************************************/

        mGeofenceManager = new StorableGeofenceManager(this);
        mGeofenceManager.setListener(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else {
            onAccessFineLocationPermissionGranted();
        }
        onDeleteAllClicked();
        onCustomClicked();
    } //end oncreate

    private DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yearOfYear, int monthOfYear, int dayOfMonth) {

            if (Flag_day == true) {
                if (0 < monthOfYear && monthOfYear < 10) {
                    start_year = yearOfYear;
                    start_month = monthOfYear + 1;
                    start_day = dayOfMonth;
                    tv_start_day.setText(yearOfYear + "년 " + "0" + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                } else {
                    tv_start_day.setText(yearOfYear + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                }
            } else {
                end_year = yearOfYear;
                end_month = monthOfYear + 1;
                end_day = dayOfMonth;
                if (0 < monthOfYear && monthOfYear < 10) {
                    tv_end_day.setText(yearOfYear + "년 " + "0" + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                } else {
                    tv_end_day.setText(yearOfYear + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                }
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfMinute) {

            if (Flag_time == true) {
                start_hour = hourOfDay;
                start_minute = minuteOfMinute;
                tv_start_time.setText(hourOfDay + "시 " + minuteOfMinute + "분");
            } else {
                end_hour = hourOfDay;
                end_minute = minuteOfMinute;
                tv_end_time.setText(hourOfDay + "시 " + minuteOfMinute + "분");
            }
        }
    };

    /*****************************************************************************************************************************/

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onAccessFineLocationPermissionGranted();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onAccessFineLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            List<String> providers = mLocationManager.getProviders(true);
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (mCurrentLocation == null || l.getAccuracy() < mCurrentLocation.getAccuracy()) {
                    mCurrentLocation = l;
                }
            }

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        }
    }

    public void onCustomClicked() {
        HashMap<String, Object> additionalData = new HashMap<>();
        additionalData.put(ADDITIONAL_DATA_TIME, new Date().getTime()); // add a long
        additionalData.put(ADDITIONAL_DATA_PACKAGE, getApplicationContext().getPackageName()); // add a String
        addGeofence(false, additionalData);
    }

    public void onDefaultClicked(View view) {
        addGeofence(true, null);
    }

    public void onDeleteAllClicked() {
        ArrayList<StorableGeofence> storedGeo = mGeofenceManager.getAllGeofences();
        for (StorableGeofence geo : storedGeo) {
            mGeofenceManager.removeGeofence(geo.getId());
        }
    }

    private void addGeofence(boolean defaultReceiver, HashMap<String, Object> additionalData) {
        String geoId;
        String receiverClassName;
        if (defaultReceiver) {
            geoId = GEOFENCE_ID_FOR_DEFAULT_RECEIVER;
            receiverClassName = null;
        } else {
            geoId = GEOFENCE_ID_FOR_CUSTOM_RECEIVER;
            receiverClassName = CustomTransitionsIntentService.class.getName();
        }

        StorableGeofence storableGeofence = new StorableGeofence(
                geoId,
                receiverClassName,
//                mCurrentLocation.getLatitude(),
//                mCurrentLocation.getLongitude(),
                35.145124,
                129.009541,
                200,
                Geofence.NEVER_EXPIRE,
                300000, // 5 minutes
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL,
                additionalData);
        boolean addedOnGoing = false;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            addedOnGoing = mGeofenceManager.addGeofence(storableGeofence);
        }
        if (!addedOnGoing) {
            Log.e(TAG, "Addition of geofence has been refused " + storableGeofence);
        }
    }

    @Override
    public void geofenceAddStatus(StorableGeofence geofence, Status status) {
        if (geofence != null) {
            if (status.isSuccess()) {
//                Toast.makeText(this, "Geofence " + geofence.getId() + " has been added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error when adding " + geofence.getId() + " : " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void geofenceRemoveStatus(String geofenceId, Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, "Geofence " + geofenceId + " has been removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error when removing " + geofenceId + " : " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed : " + location);
        if (location != null) {
            mCurrentLocation = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Please enable your gps", Toast.LENGTH_SHORT).show();
    }

}

package com.example.user.userapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.sousoum.libgeofencehelper.StorableGeofence;
import com.sousoum.libgeofencehelper.StorableGeofenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener, StorableGeofenceManager.StorableGeofenceManagerListener, SwipeRefreshLayout.OnRefreshListener {

    Handler myHandler = null;

    Toast toast, toast2, toast3;

    public static String identifier2[] = new String[100];
    public static String startday2[] = new String[100];
    public static String starttime2[] = new String[100];
    public static String endday2[] = new String[100];
    public static String endtime2[] = new String[100];
    public static String reason2[] = new String[100];
    public static String allow2[] = new String[100];
    public static String notify[] = new String[100];

    private SwipeRefreshLayout swipeRefreshLayout;

    LinearLayout tab_req_lay, tab_list_lay;

    TextView tab_request, tab_list;
    //    TextView tab_request2, tab_list2;
    TextView tv_start_day, tv_start_time, tv_end_day, tv_end_time;

    EditText edt_input;

    Button btn_work, btn_eat, btn_etc, btn_request;

    DatePickerDialog date_dialog;
    TimePickerDialog time_dialog;

    int start_year, start_month, start_day, start_hour, start_minute = 0;
    int end_year, end_month, end_day, end_hour, end_minute = 0;
    String phoneNumber, reason = null;

    boolean Flag_day, Flag_time;

    ListView listview;
    ListViewAdapter adapter;

    ArrayList<String> set_identifier = new ArrayList<>();
    ArrayList<String> set_startday = new ArrayList<>();
    ArrayList<String> set_starttime = new ArrayList<>();
    ArrayList<String> set_endday = new ArrayList<>();
    ArrayList<String> set_endtime = new ArrayList<>();
    ArrayList<String> set_reason = new ArrayList<>();
    ArrayList<String> set_allow = new ArrayList<>();
    ArrayList<String> set_time = new ArrayList<>();

//    BackgroundTask task;
//    BackgroundTask2 task2;

    String address, result;
    String flag = "SELECT";

    public static JSONArray ja;
    Thread thread;
    private boolean stopflag = false;

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

        myHandler = new Handler();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        tab_req_lay = (LinearLayout) findViewById(R.id.tab_req_lay);
        tab_list_lay = (LinearLayout) findViewById(R.id.tab_list_lay);

        tab_request = (TextView) findViewById(R.id.tab_request);
//        tab_request2 = (TextView) findViewById(R.id.tab_request2);
        tab_list = (TextView) findViewById(R.id.tab_list);
//        tab_list2 = (TextView) findViewById(R.id.tab_list2);

        tv_start_day = (TextView) findViewById(R.id.start_day);
        tv_start_time = (TextView) findViewById(R.id.start_time);
        tv_end_day = (TextView) findViewById(R.id.end_day);
        tv_end_time = (TextView) findViewById(R.id.end_time);

        edt_input = (EditText) findViewById(R.id.edt_input);

        btn_work = (Button) findViewById(R.id.btn_work);
        btn_eat = (Button) findViewById(R.id.btn_eat);
        btn_etc = (Button) findViewById(R.id.btn_etc);
        btn_request = (Button) findViewById(R.id.btn_request);

        adapter = new ListViewAdapter();
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        toast = Toast.makeText(this, "이미 신청한 외출 정보가 있습니다", Toast.LENGTH_SHORT);
        toast2 = Toast.makeText(this, "외출사유를 선택하세요", Toast.LENGTH_SHORT);
        toast3 = Toast.makeText(this, "외출시간을 확인하세요", Toast.LENGTH_SHORT);

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

        tab_list_lay.setEnabled(false);

        start_year = strCurYear;
        start_month = strCurMonth;
        start_day = strCurDay;
        start_hour = strCurHour;
        start_minute = strCurMinute;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);
                String start = item.getStart();
                String descStr = item.getDesc();
                String reason = item.getReason();
                Log.d("QQQ", "QQQ");
            }
        });

        tab_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab_request.setBackgroundColor(Color.rgb(222, 222, 222));
                tab_list.setBackgroundColor(Color.rgb(255, 255, 255));
                tab_request.setTextColor(Color.rgb(1, 94, 125));
                tab_list.setTextColor(Color.rgb(0, 0, 0));
                tab_req_lay.setVisibility(View.VISIBLE);
                tab_list_lay.setVisibility(View.GONE);
            }
        });

        tab_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab_request.setBackgroundColor(Color.rgb(255, 255, 255));
                tab_list.setBackgroundColor(Color.rgb(222, 222, 222));
                tab_request.setTextColor(Color.rgb(0, 0, 0));
                tab_list.setTextColor(Color.rgb(1, 94, 125));
                tab_req_lay.setVisibility(View.GONE);
                tab_list_lay.setVisibility(View.VISIBLE);
            }
        });

        tv_start_day.setText(strCurDate);
        tv_start_time.setText(strCurTime);
//        tv_end_day.setText(strCurDate);
//        tv_end_time.setText(strCurTime);
        tv_end_day.setText("...");
        tv_end_time.setText("...");

        /*if (strCurHour + 3 < 24) {
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
        }*/

        date_dialog = new DatePickerDialog(this, date_listener, strCurYear, strCurMonth - 1, strCurDay);
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
                reason = "업무";
                btn_eat.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_work.setBackgroundColor(Color.rgb(217, 217, 217));
                btn_request.setEnabled(true);
                btn_request.setBackgroundColor(Color.rgb(250, 250, 0));
                btn_request.setTextColor(Color.rgb(59, 30, 30));
                edt_input.setHint("");
                edt_input.setEnabled(false);
            }
        });

        btn_eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = "식사";
                btn_work.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_eat.setBackgroundColor(Color.rgb(217, 217, 217));
                btn_request.setEnabled(true);
                btn_request.setBackgroundColor(Color.rgb(250, 250, 0));
                btn_request.setTextColor(Color.rgb(59, 30, 30));
                edt_input.setHint("");
                edt_input.setEnabled(false);
            }
        });

        btn_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = "기타";
                btn_work.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_eat.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(217, 217, 217));
                btn_request.setEnabled(true);
                btn_request.setBackgroundColor(Color.rgb(250, 250, 0));
                btn_request.setTextColor(Color.rgb(59, 30, 30));
                edt_input.setEnabled(true);
                edt_input.setHint("직접입력...");
            }
        });

        // 외출신청
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myPhone = phoneNum(MainActivity.this);
//                select2("http://192.168.64.166:3000/data");
                String T_start_day = tv_start_day.getText().toString();
                String T_start_time = tv_start_day.getText().toString();
                String T_end_day = tv_start_day.getText().toString();
                String T_end_time = tv_start_day.getText().toString();
//                if (start_year != 0 && start_month != 0 && start_day != 0 && start_hour != 0 && start_minute != 0) {
//                    if (end_year != 0 && end_month != 0 && end_day != 0 && end_hour != 0 && end_minute != 0) {
                if (reason != null) {

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                    String nowDate = format.format(date);
                    Date day1 = null;
                    Date day2 = null;
                    Date day3 = null;
                    try {
                        day1 = format.parse(nowDate);
                        day2 = format.parse(start_year + "-" + start_month + "-" + start_day + "-" + start_hour + "-" + start_minute);
                        day3 = format.parse(end_year + "-" + end_month + "-" + end_day + "-" + end_hour + "-" + end_minute);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int compare = day1.compareTo(day2);
                    int compare2 = day2.compareTo(day3);
                    if (compare > 0) {
                        Log.d("TAG", "!!!!!!!!!!!!");
                        toast3.setGravity(Gravity.CENTER, 0, 0);
                        toast3.show();
                        return;
                    } else {
                        if (compare2 > 0) {
                            Log.d("TAG", "@@@@@@@@@@@");
                            toast3.setGravity(Gravity.CENTER, 0, 0);
                            toast3.show();
                            return;
                        } else {
//                            Log.d("length=", "" + ja.length());
                            for (int i = 0; i < ja.length(); i++) {
//                                Log.d("identifier=", "" + identifier2[i]);
//                                Log.d("phone=", myPhone);
                                if (identifier2[i].equals(myPhone) && allow2[i].equals("대기")) {
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                } else if (identifier2[i].equals(myPhone) && allow2[i].equals("승인")) {
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    return;
                                }
                            }
                            btn_request.setEnabled(false);
                            edt_input.setEnabled(false);
                            stopflag = false;
                            btn_request.setBackgroundColor(Color.rgb(230, 230, 230));
                            btn_request.setTextColor(Color.rgb(165, 165, 165));

                            btn_eat.setBackgroundColor(Color.rgb(242, 242, 242));
                            btn_work.setBackgroundColor(Color.rgb(242, 242, 242));
                            btn_etc.setBackgroundColor(Color.rgb(242, 242, 242));

                            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy년 MM월 dd일");
                            SimpleDateFormat sdfNow2 = new SimpleDateFormat("HH시 mm분");
                            String strNow = sdfNow.format(date);
                            String strNow2 = sdfNow2.format(date);

                            tv_start_day.setText(strNow);
                            tv_start_time.setText(strNow2);
                            tv_end_day.setText("...");
                            tv_end_time.setText("...");

                            thread = new Thread() {
                                public void run() {
                                    while (!stopflag) {
                                        long now = System.currentTimeMillis();
                                        Date date = new Date(now);
                                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat sdfNow2 = new SimpleDateFormat("HH:mm:ss");
                                        String strNow = sdfNow.format(date);
                                        String strNow2 = sdfNow2.format(date);

                                        String startDay = start_year + "/" + start_month + "/" + start_day;
                                        String startTime = start_hour + ":" + start_minute;
                                        String endDay = end_year + "/" + end_month + "/" + end_day;
                                        String endTime = end_hour + ":" + end_minute;

                                        try {
                                            reason = java.net.URLEncoder.encode(new String(reason.getBytes("UTF-8")));
                                        } catch (Exception e) {
                                            Log.d("ERROR reason encoder", "ERROR reason encoder");
                                        }

//                            won_insert("http://192.168.64.166:3000/request?identifier=" + phoneNumber + "&" + "startday=" + startDay + "&" + "starttime=" + startTime + "&" + "endday=" + endDay + "&" + "endtime=" + endTime + "&" + "reason=" + reason + "&" + "time=" + strNow + "&" + "time2=" + strNow2);
                                        won_insert("http://192.168.64.166:3000/request?identifier=" + phoneNumber + "&" + "startday=" + startDay + "&" + "starttime=" + startTime + "&" + "endday=" + endDay + "&" + "endtime=" + endTime + "&" + "reason=" + reason + "&" + "time=" + strNow + "&" + "time2=" + strNow2);
//                                                won_insert("http://112.185.119.215:3000/request?identifier=" + phoneNumber + "&" + "startday=" + startDay + "&" + "starttime=" + startTime + "&" + "endday=" + endDay + "&" + "endtime=" + endTime + "&" + "reason=" + reason + "&" + "time=" + strNow + "&" + "time2=" + strNow2);

                                        final SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                                        SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH시 mm분");
                                        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
                                        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
                                        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
                                        SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
                                        SimpleDateFormat CurMinuteFormat = new SimpleDateFormat("mm");

                                        int strCurYear = Integer.parseInt(CurYearFormat.format(date));
                                        int strCurMonth = Integer.parseInt(CurMonthFormat.format(date));
                                        int strCurDay = Integer.parseInt(CurDayFormat.format(date));
                                        int strCurHour = Integer.parseInt(CurHourFormat.format(date));
                                        int strCurMinute = Integer.parseInt(CurMinuteFormat.format(date));

                                        start_year = strCurYear;
                                        start_month = strCurMonth;
                                        start_day = strCurDay;
                                        start_hour = strCurHour;
                                        start_minute = strCurMinute;

                                        end_year = 0;
                                        end_month = 0;
                                        end_day = 0;
                                        end_hour = 0;
                                        end_minute = 0;

                                        reason = null;
                                    }
                                }
                            };
                            thread.start();
                        }
                    }
                } else {
                    toast2.setGravity(Gravity.CENTER, 0, 0);
                    toast2.show();
                }
                    /*} else {
                        Log.d("TAG", "############");
                        toast3.setGravity(Gravity.CENTER, 0, 0);
                        toast3.show();
                    }
                } else {
                    Log.d("TAG", "$$$$$$$$$$");
                    toast3.setGravity(Gravity.CENTER, 0, 0);
                    toast3.show();
                }*/
            }
        });

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

        patchEOFException(); // EOFException 에러

//        onCustomClicked(); // 지오펜스 실행

        phoneNumber = phoneNum(MainActivity.this);

        //신청목록 조회
        new Thread() {
            public void run() {
                won_select("http://192.168.64.166:3000/data");
                geo_select("http://192.168.64.166:3000/setGeoData");
//                won_select("http://112.185.119.215:3000/data");

                /*if (checkGPS()) {
                    Log.d("PN", "" + phoneNumber);
                    loc_update("http://192.168.64.166:3000/user_gps?phonenum=" + phoneNumber + "&gps=ON");
                } else {
                    loc_update("http://192.168.64.166:3000/user_gps?phonenum=" + phoneNumber + "&gps=OFF");
                }*/
            }
        }.start();

        Intent intent = new Intent(MainActivity.this, MyService.class);
        startService(intent);

    } //onCreate 종료


    /***********************************************************************************************************************************/

    @Override
    public void onRefresh() {
        new Thread() {
            public void run() {
                won_select("http://192.168.64.166:3000/data");
//                won_select("http://112.185.119.215:3000/data");
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }.start();
    }

    /*public boolean checkGPS() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPS) {
            return true;
        } else {
            Log.d("GPS", "CHECK");
        }
        return false;
    }*/

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

    public void onBackPressed() {
        onDeleteAllClicked();
        super.onBackPressed();
    }

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

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    public void onCustomClicked(double lat, double lon, int radius) {
        HashMap<String, Object> additionalData = new HashMap<>();
        additionalData.put(ADDITIONAL_DATA_TIME, new Date().getTime()); // add a long
        additionalData.put(ADDITIONAL_DATA_PACKAGE, getApplicationContext().getPackageName()); // add a String
        addGeofence(false, additionalData, lat, lon, radius);
    }

    /*public void onDefaultClicked(View view) {
        addGeofence(true, null);
    }*/

    public void onDeleteAllClicked() {
        ArrayList<StorableGeofence> storedGeo = mGeofenceManager.getAllGeofences();
        for (StorableGeofence geo : storedGeo) {
            mGeofenceManager.removeGeofence(geo.getId());
        }
    }

    private void addGeofence(boolean defaultReceiver, HashMap<String, Object> additionalData, double lat, double lon, int radius) {
        Log.d("GEO ADD", "GEO ADD");
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
                lat,
                lon,
                radius,
                Geofence.NEVER_EXPIRE,
                30000000, // 5 minutes
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
                Toast.makeText(this, "add Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void geofenceRemoveStatus(String geofenceId, Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, "remove", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "remove Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), "change", Toast.LENGTH_SHORT).show();
        try {
            loc_update("http://192.168.64.166:3000/user_loc?phonenum=" + phoneNumber + "&lat=" + location.getLatitude() + "&lon=" + location.getLongitude());
//            loc_update("http://112.185.119.215:3000/user_loc?phonenum=" + phoneNumber + "&lat=" + location.getLatitude() + "&lon=" + location.getLongitude());
        } catch (Exception e) {
            Log.d("changed error", "changed error");
        }
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

    // 디바이스 식별번호
    /*private String GetDevicesUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }*/

    // 전화번호
    public String phoneNum(Context context) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String phoneNum = telManager.getLine1Number();
            if (phoneNum != null) {
                phoneNum = phoneNum.replaceFirst("\\+82", "0");
            }
            return phoneNum;
        } else {
            //Manifest.permission.READ_CALENDAR이 접근 거절 상태 일때

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }
        return null;
    }

    private void patchEOFException() {
        System.setProperty("http.keepAlive", "false");
    }

    public void won_insert(String link) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(20000);
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
            Log.d("ERROR", "ERROR");
        } finally {
            stopflag = true;
            Log.d("flag=", "true");
            reason = null;
        }
    }

    public void won_select(String link) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(2000);
                conn.setUseCaches(false);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
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
            Log.d("ERROR", "ERROR");
        }

        adapter.remove();

        String jsonString = sb.toString();
        try {
            ja = new JSONArray(jsonString);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String identifier[] = new String[100];
                String startday[] = new String[100];
                String starttime[] = new String[100];
                String endday[] = new String[100];
                String endtime[] = new String[100];
                String reason[] = new String[100];
                String allow[] = new String[100];
                identifier[i] = jo.getString("identifier");
                startday[i] = jo.getString("startday");
                starttime[i] = jo.getString("starttime");
                endday[i] = jo.getString("endday");
                endtime[i] = jo.getString("endtime");
                reason[i] = jo.getString("reason");
                allow[i] = jo.getString("allow");

//                Log.d("TAG", phoneNum(this));
//                Log.d("TAG", identifier[i]);
                if (phoneNum(this).equals(identifier[i])) {
//                    Log.d("TAG", "OK");
                    adapter.addItem(startday[i] + "  " + starttime[i], endday[i] + "  " + endtime[i], reason[i], allow[i]);
                }

            }
        } catch (JSONException e) {
            Log.d("ERROR", "ERROR");
        }
    }

    public void loc_update(String link) {
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
            Log.d("ERROR latlon", "ERROR latlon");
        }
    }

    public void geo_select(String link) {
        Log.d("select", "select");
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
            Log.d("ERROR geo select", "ERROR geo select");
        }

        String jsonString = sb.toString();
        try {
            ja = new JSONArray(jsonString);
            String lat[] = new String[100];
            String lon[] = new String[100];
            String radius[] = new String[100];
            double _lat, _lon = 0;
            int _radius = 0;
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                lat[i] = jo.getString("lat");
                lon[i] = jo.getString("lon");
                radius[i] = jo.getString("radius");
                _lat = Double.parseDouble(lat[i]);
                _lon = Double.parseDouble(lon[i]);
                _radius = Integer.parseInt(radius[i]);
                onCustomClicked(_lat, _lon, _radius);
                Log.d("TAG", lat[i]);
                Log.d("TAG", lon[i]);
                Log.d("TAG", radius[i]);
            }

        } catch (JSONException e) {
            Log.d("ERROR", "ERROR");
        }
    }

    /*public void select2(String link) {
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
            ja = new JSONArray(jsonString);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                identifier2[i] = jo.getString("identifier");
                startday2[i] = jo.getString("startday");
                starttime2[i] = jo.getString("starttime");
                endday2[i] = jo.getString("endday");
                endtime2[i] = jo.getString("endtime");
                reason2[i] = jo.getString("reason");
                allow2[i] = jo.getString("allow");
            }
        } catch (JSONException e) {
            Log.d("ERROR", "ERROR");
        }
    }*/

}
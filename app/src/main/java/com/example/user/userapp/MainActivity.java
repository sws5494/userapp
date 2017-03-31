package com.example.user.userapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView tv_start_day, tv_start_time, tv_end_day, tv_end_time;
    Button btn_work, btn_eat, btn_etc, btn_request;

    DatePickerDialog date_dialog;
    TimePickerDialog time_dialog;

    int start_year, start_month, start_day, start_hour, start_minute;
    int end_year, end_month, end_day, end_hour, end_minute;
    int reason;

    boolean Flag_day, Flag_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        tv_start_day.setText(strCurDate);
        tv_start_time.setText(strCurTime);
        tv_end_day.setText(strCurDate);
        if (strCurHour + 3 < 24) {
            tv_end_time.setText(strCurTime+3);
        }else{
            tv_end_time.setText(strCurTime);
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
                    return ;
                }
                Toast.makeText(getApplicationContext(), "시작: " + start_year + "/" + start_month + "/" + start_day + "/" + start_hour + "/" + start_minute, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "종료: " + end_year + "/" + end_month + "/" + end_day + "/" + end_hour + "/" + end_minute, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "사유: " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

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
}

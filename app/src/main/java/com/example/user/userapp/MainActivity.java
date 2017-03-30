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
    TextView start_day, start_time, end_day, end_time;
    Button btn_work, btn_eat, btn_etc;

    DatePickerDialog date_dialog;
    TimePickerDialog time_dialog;

    int month, day, hour, minute;
    boolean Flag_day, Flag_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start_day = (TextView) findViewById(R.id.start_day);
        start_time = (TextView) findViewById(R.id.start_time);
        end_day = (TextView) findViewById(R.id.end_day);
        end_time = (TextView) findViewById(R.id.end_time);

        btn_work = (Button) findViewById(R.id.btn_work);
        btn_eat = (Button) findViewById(R.id.btn_eat);
        btn_etc = (Button) findViewById(R.id.btn_etc);

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

        start_day.setText(strCurDate);
        start_time.setText(strCurTime);
        end_day.setText(strCurDate);
        end_time.setText(strCurTime);

        date_dialog = new DatePickerDialog(this, date_listener, strCurYear, strCurMonth, strCurDay);
        time_dialog = new TimePickerDialog(this, time_listener, strCurHour, strCurMinute, false);

        start_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_day = true;
                date_dialog.show();

            }
        });
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_time = true;
                time_dialog.show();
            }
        });
        end_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_day = false;
                date_dialog.show();
            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Flag_time = false;
                time_dialog.show();
            }
        });

        btn_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_eat.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_work.setBackgroundColor(Color.rgb(217, 217, 217));
            }
        });
        btn_eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_work.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_eat.setBackgroundColor(Color.rgb(217, 217, 217));
            }
        });
        btn_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_work.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_eat.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_etc.setBackgroundColor(Color.rgb(217, 217, 217));
            }
        });
    }

    private DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (Flag_day == true) {
                if (0 < monthOfYear && monthOfYear < 10) {
                    start_day.setText(year + "년 " + "0" + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                } else {
                    start_day.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                }
            } else {
                if (0 < monthOfYear && monthOfYear < 10) {
                    end_day.setText(year + "년 " + "0" + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                } else {
                    end_day.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                }
            }
//            Toast.makeText(getApplicationContext(), year + "년" + (monthOfYear + 1) + "월" + dayOfMonth + "일", Toast.LENGTH_SHORT).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (Flag_time == true) {
                start_time.setText(hourOfDay + "시 " + minute + "분");
            } else {
                end_time.setText(hourOfDay + "시 " + minute + "분");
            }
//            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
        }
    };
}

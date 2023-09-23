package com.example.thraedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;





public class  MainActivity extends AppCompatActivity implements LocationListener{

    int hour;

    private LocationManager locationManager;
    private Location mLastlocation = null;
    int value = 0;

    int hour_flag = 0;
    String temp1;
    int Speed;

    double dt;
    double sum_list;
    List<Double> listA = new ArrayList<>();

    double carbonAmount;

    String logFlag;
    String getTime;
    TextView tvCarbonAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvCarbonAmount = findViewById(R.id.tv_carbonAmount);
        carbonAmount = 0.0;
        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        }
        // GPS 사용 가능 여부 확인
        boolean isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);


        BackgroundThread thread = new BackgroundThread();
        thread.start();
        ViewThread thread2 = new ViewThread();
        thread2.start();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        double deltaTime;

        //  getSpeed() 함수를 이용하여 속도를 계산
        @SuppressLint("DefaultLocale") double getSpeed = Double.parseDouble(String.format("%.3f", location.getSpeed()));
        String formatDate = sdf.format(new Date(location.getTime()));


        Speed = (int) getSpeed;

        if (mLastlocation != null) {
            //시간 간격
            deltaTime = (location.getTime() - mLastlocation.getTime()) / 1000.0;
            // 속도 계산
            double speed = mLastlocation.distanceTo(location) / deltaTime;

        }
        /* 현재위치를 지난 위치로 변경 */
        mLastlocation = location;
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // 위치정보 업데이트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }

    class ViewThread extends Thread {
        public void run() {
            while (true) {
                try {Thread.sleep(1000);} catch (Exception e) {}
                String temp = "[";
                for(int i=0; i<listA.size(); i++) temp += String.valueOf(listA.get(i)) + ", ";
                temp += "]";
                Log.i("VAL", "ITEMS:" + temp);

            }
        }
    }

    int sec;
    class BackgroundThread extends Thread {
        public void run(){
            while (true){
                try {
                    Thread.sleep(1000);
                }catch(Exception e){ }

                Calendar cToday = Calendar.getInstance();

                sec = cToday.get(Calendar.SECOND);
                if(Speed <= 7) listA.add((double)Speed * 3.6);

                if(sec % 10 == 0 && Speed <= 7) {
                    sum_list = 0;
                    for(int i = 0; i < listA.size(); i++)
                        sum_list += listA.get(i);
                    carbonAmount += ((sum_list/10) * 0.002) * 0.1;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCarbonAmount.setText(temp1);
                        }
                    });
                    temp1 = (String.valueOf(carbonAmount)).substring(0,3);
                    Log.i("VAL", "carbonAmount:" + temp1);
                    Log.i("VAL", "carbonAmount:" + carbonAmount);
                    listA.clear();
                }
            }
        }
    }
}

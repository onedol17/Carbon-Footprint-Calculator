package com.example.thraedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;





public class  MainActivity extends AppCompatActivity implements LocationListener{
    private LocationManager locationManager;
    private Location mLastlocation = null;

    String temp1;
    int Speed;

    double sum_list;
    List<Double> listA = new ArrayList<>();

    double carbonAmount=0.0;
    FrameLayout bg;
    TextView tvCarbonAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCarbonAmount = findViewById(R.id.tv_carbonAmount);
        bg = findViewById(R.id.background);
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

        // list 관리 스레드
        ListAddThread lt = new ListAddThread();
        lt.start();

        // list 계산 스레드
        CalcThread ct = new CalcThread();
        ct.start();
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

    @Override
    protected void onResume() {
        super.onResume();
        readData();
    }

    class CalcThread extends Thread {
        public void run() {
            while (true) {
                try {Thread.sleep(1000);} catch (Exception e) {}
                
                // 리스트 로그로 확인
                String temp = "[";
                for(int i=0; i<listA.size(); i++) temp += String.valueOf(listA.get(i)) + ", ";
                temp += "]";
                Log.i("VAL", "ITEMS:" + temp);
                
                
                // 탄소 소모량 계산
                Calendar cToday = Calendar.getInstance();
                sec = cToday.get(Calendar.SECOND);
                if(sec % 10 == 0) {
                    sum_list = 0;
                    
                    for(int i = 0; i < listA.size(); i++)
                        sum_list += listA.get(i);
                    
                    carbonAmount += ((sum_list/10) * 0.002) * 0.1;
                    temp1 = (String.valueOf(carbonAmount)).substring(0,3);
                    
                    Log.i("VAL", "carbonAmount:" + temp1);

                    // UI 표출
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCarbonAmount.setText(temp1);
                            // TODO : tree 베어내는것을 표현하는 곳

                        }
                    });

                    writeData();
                    listA.clear();
                }
            }
        }
    }

    int sec;
    class ListAddThread extends Thread {
        public void run(){
            while (true){
                try {
                    Thread.sleep(1000);
                }catch(Exception e){ }
                
                if (Speed >= 7) listA.add((double)Speed * 3.6);
            }
        }
    }

    private void writeData() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + "text.txt", false));

            bw.write(String.valueOf(carbonAmount));
            bw.close();
            Log.d("RB", String.valueOf(getFilesDir()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readData() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir() + "text.txt"));
            String readStr ="";
            String str = null;
            while((str=br.readLine())!=null) readStr +=str +"\n";
            br.close();
            Log.d("RB", readStr);
            carbonAmount = Double.parseDouble(readStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

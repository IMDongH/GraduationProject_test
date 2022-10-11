package com.example.graduationprojecttest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;


public class StartActivity extends AppCompatActivity implements SensorEventListener {

    private TextView loacationText;
    private TextView walkingText;
    private TextView timerText;
    private Button timerPause;
    private Button timerStop;

    Handler handler;
    int Seconds, Minutes, MilliSeconds;
    long MillisecondTime = 0L;  // 스탑워치 시작 버튼을 누르고 흐른 시간
    long StartTime = 0L;        // 스탑워치 시작 버튼 누르고 난 이후 부터의 시간
    long TimeBuff = 0L;         // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간
    long UpdateTime = 0L;       // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간 + 시작 버튼 누르고 난 이후 부터의 시간 = 총 시간

    SensorManager sensorManager;
    Sensor stepCountSensor;

    // 현재 걸음 수
    int currentSteps = 0;

    //흐른 시간
    String time;

    //현재 날짜 및 시간
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    WalkingRecord walkingRecord = new WalkingRecord();

    private boolean flag = false;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        handler = new Handler() ;

        loacationText = (TextView)findViewById(R.id.loactionText);
        walkingText = (TextView)findViewById(R.id.walkingText);

        timerText = (TextView)findViewById(R.id.timerText);
        timerPause = (Button)findViewById(R.id.timerPause);
        timerStop = (Button)findViewById(R.id.timerStop);

        // SystemClock.uptimeMillis()는 디바이스를 부팅한후 부터 쉰 시간을 제외한 밀리초를 반환
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);


        // 활동 퍼미션 체크
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // 걸음 센서 연결
        // * 옵션
        // - TYPE_STEP_DETECTOR:  리턴 값이 무조건 1, 앱이 종료되면 다시 0부터 시작
        // - TYPE_STEP_COUNTER : 앱 종료와 관계없이 계속 기존의 값을 가지고 있다가 1씩 증가한 값을 리턴
        //
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        startLocationService();

        // 스탑워치 일시정시 버튼
        timerPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간
                if(!flag) {
                    TimeBuff += MillisecondTime;
                    flag = !flag;
                    // Runnable 객체 제거
                    handler.removeCallbacks(runnable);
                }
                else{
                    // SystemClock.uptimeMillis()는 디바이스를 부팅한후 부터 쉰 시간을 제외한 밀리초를 반환
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    flag = !flag;
                }
            }
        });


        // 스탑워치 스탑 버튼
        timerStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeBuff += MillisecondTime;
                // Runnable 객체 제거
                handler.removeCallbacks(runnable);
                ArrayList<WalkingDTO> record = walkingRecord.getRecord();

                for (WalkingDTO data : record) {
                    System.out.println(data.getTime() +" = " + data.toString());
                }
            }
        });

    }


    public Runnable runnable = new Runnable() {

        public void run() {

            // 디바이스를 부팅한 후 부터 현재까지 시간 - 시작 버튼을 누른 시간
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간 + 시작 버튼 누르고 난 이후 부터의 시간 = 총 시간
            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            // TextView에 UpdateTime을 갱신해준다
            time = Minutes + ":"+ String.format("%02d", Seconds);
            timerText.setText(time);

            handler.postDelayed(this, 0);
        }

    };

    public void startLocationService(){

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions( StartActivity.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        else{
            // 가장최근 위치정보 가져오기
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null && !flag) {
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();

                loacationText.setText("위치정보 : " + provider + "\n" +
                        "위도 : " + longitude + "\n" +
                        "경도 : " + latitude + "\n" +
                        "고도  : " + altitude);

                // 위치정보를 원하는 시간, 거리마다 갱신해준다.
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000, //1초마다
                        0,
                        gpsLocationListener);
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        1000,
                        0,
                        gpsLocationListener);
            }


        }
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현

            String provider = location.getProvider();  // 위치정보
            double longitude = location.getLongitude(); // 위도
            double latitude = location.getLatitude(); // 경도
            double altitude = location.getAltitude(); // 고도

            loacationText.setText("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도 : " + altitude);

            walkingRecord.addRecord(new WalkingDTO(longitude,latitude,altitude,getTime(),time,currentSteps));

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        } public void onProviderEnabled(String provider) {

        } public void onProviderDisabled(String provider) {

        }
    };

    public void onStart() {
        super.onStart();
        if(stepCountSensor !=null) {
            // 센서 속도 설정
            // * 옵션
            // - SENSOR_DELAY_NORMAL: 20,000 초 딜레이
            // - SENSOR_DELAY_UI: 6,000 초 딜레이
            // - SENSOR_DELAY_GAME: 20,000 초 딜레이
            // - SENSOR_DELAY_FASTEST: 딜레이 없음
            //
            sensorManager.registerListener(this,stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // 걸음 센서 이벤트 발생시
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){

            if(event.values[0]==1.0f && !flag){
                // 센서 이벤트가 발생할때 마다 걸음수 증가
                currentSteps++;
                walkingText.setText(String.valueOf(currentSteps));
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private String getTime(){
        long mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

}
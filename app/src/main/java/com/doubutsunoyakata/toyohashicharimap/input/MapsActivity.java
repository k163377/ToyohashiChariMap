package com.doubutsunoyakata.toyohashicharimap.input;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.doubutsunoyakata.toyohashicharimap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //map
    private GoogleMap mMap;
    //gps関連
    private LocationManager locationManager;
    private Location currentLocation = null;
    //タイマー処理に関する部分
    final int INTERVAL_PERIOD = 5000;//インターバル
    Timer timer = null;

    //データ
    ReviewData rd;

    //現在地を取得する関数
    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) continue;
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void startRecording(){
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {//権限がまだ無い場合
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {//明示的に権限が拒否されていた時
                //拒否されていた時の処理、なんかしらのメッセージを出すと良いと思われる、今は何も考えずにパーミッションを聞いておく
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {//まだ聞いてなかったとき
                //与えても良いか聞く、onRequestPermissionsResultが答えを受ける
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                currentLocation = getLastKnownLocation();
                if(currentLocation == null){
                    //ModeButton.setText("null");
                }
                else {
                    Log.d("Timer", currentLocation.toString());
                    LatLng p = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    rd.addLatLng(p);

                    //mMap.addMarker(new MarkerOptions().position(p).title("Start"));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 15));
                }
            }
        }, 0, INTERVAL_PERIOD);
    }
    private void stopRecording(){
        if(timer != null) {
            Log.d("StopButton", "stop!");
            timer.cancel();
            Polyline polyline = mMap.addPolyline(rd.getPolylineOptions());

            if(currentLocation != null) {
                LatLng p = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(p).title("Stop"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 15));
            }
        }
        else Log.d("StopButton", "timer null");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: { //requestPermissions()の第2引数で指定した値
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//許可された場合
                }else{//拒否された場合の処理
                }
                break;
            }
        }
    }

    //ライフサイクル
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //ModeButton = (Button) findViewById(R.id.ModeButton);
        /*ModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        StopButton = (Button) findViewById(R.id.StopButton);
        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });*/

        rd = new ReviewData("Dummy ID");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //権限の確認は、実際には使う場面でやるべきで、onCreateでやるのは不適
        //GPS権限の確認
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {//権限がまだ無い場合
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {//明示的に権限が拒否されていた時
                //拒否されていた時の処理、なんかしらのメッセージを出すと良いと思われる、今は何も考えずにパーミッションを聞いておく
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {//まだ聞いてなかったとき
                //与えても良いか聞く、onRequestPermissionsResultが答えを受ける
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else {//権限がある場合
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
        Log.d("Timer", "onDestroy");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //マップ関連
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ToyohashiStation = new LatLng(34.7628819, 137.3819014);
        //mMap.addMarker(new MarkerOptions().position(ToyohashiStation).title("ToyohashiStation"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ToyohashiStation, 15));
    }
}
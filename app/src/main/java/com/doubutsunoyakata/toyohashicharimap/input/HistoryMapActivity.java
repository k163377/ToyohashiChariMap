package com.doubutsunoyakata.toyohashicharimap.input;


import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.doubutsunoyakata.toyohashicharimap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class HistoryMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //gps関連
    private LocationManager locationManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        HistoryActivity ha = new HistoryActivity();
        List<String> fileList = ha.getFileList();

        for(String fileName: fileList) {
            ReviewData rd = ReviewData.fileDeserialize(this, fileName);
            //マーカを地図上に描画
            for(MarkerOptions mo : rd.getMarkerOptions()){
                mMap.addMarker(mo);
            }
            //選択した地点を線で結ぶ
            PolylineOptions polyOptions = rd.getPolylineOptions();
            if(polyOptions != null) {
                mMap.addPolyline(polyOptions);
            }
        }

        Location l = getLastKnownLocation();
        LatLng p;

        if(l != null){
            p = new LatLng(l.getLatitude(), l.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 15));
        }else{
            //取れなかったら豊橋駅に視点を移す
            p = new LatLng(34.7628819, 137.3819014);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 15));
        }
    }
}

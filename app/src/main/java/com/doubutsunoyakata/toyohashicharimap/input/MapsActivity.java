package com.doubutsunoyakata.toyohashicharimap.input;

import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doubutsunoyakata.toyohashicharimap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
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

    //レイアウト
    Button ConfirmButton;

    Button undo, redo;
    public int f = 1;

    //Polylineの再描画用(描画時に毎回deleteするために前回のpolyline情報を格納しておく)
    private Polyline lastPolyLine = null;
    //markerの保存用ArrayList
    private MarkerOptions lastMarker = null;

    //データ
    //ReviewData rd = new ReviewData("test");
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
    //位置の記録
    private void startRecording(){
        //一定時間ごとに
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

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 15));

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

    //ライフサイクル
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rd = new ReviewData("Dummy ID");

        ConfirmButton = (Button) findViewById(R.id.ConfirmButton);
        ConfirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final EditText editView = new EditText(MapsActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
                dialog.setTitle("レビューを入力");
                dialog.setView(editView);

                //投稿ボタン
                dialog.setPositiveButton("投稿", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //後で関数として書き換え
                        rd.setReview(editView.getText().toString());
                        //rd.outputSerialize(MapsActivity.this);
                        //投稿はまだ実装しない
                    }
                });
                //下書き保存
                dialog.setNeutralButton("下書き保存", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //後で関数として書き換え
                        rd.setReview(editView.getText().toString());
                        //rd.outputSerialize(MapsActivity.this);
                    }
                });
                dialog.show();
            }
        });*/

        //undoボタンの処理
        undo = (Button) findViewById(R.id.UndoButton);
        undo.setEnabled(false); //最初は何もしていないので，ボタン利用不可にしてある
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentIndexのundo操作
                rd.undoCurrentIndex();
                //最後に描画した直線の削除
                if(lastPolyLine != null){
                    lastPolyLine.remove();
                }
                //mapのクリア(最後に付けたマーカの削除)
                mMap.clear();
                //currentIndexが0まで戻したら，これ以上戻れないのでボタンを利用不可にする
                if(rd.getCurrentIndex() != 0) {
                    //マーカの再描画
                    for (MarkerOptions mo : rd.getMarkerOptions()) {
                        mMap.addMarker(mo);
                    }
                    //マーカを再接続(直線の描画)
                    if (rd.getCurrentIndex() < 2) {
                        lastPolyLine = null;    //マーカの地点が2つ以上ないと結べないので，nullを入れてある
                    } else {
                        lastPolyLine = mMap.addPolyline(rd.getPolylineOptions());
                    }
                }else{
                    undo.setEnabled(false);
                }
                //redoボタンを利用可能にする
                redo.setEnabled(true);
            }
        });

        //redoボタンの処理
        redo = (Button) findViewById(R.id.RedoButton);
        redo.setEnabled(false); //最初は何もしていないので，ボタン利用不可にしてある
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentIndexのredo操作
                rd.redoCurrentIndex();
                //mapのクリア(一度リセットしてから再描画，もしかしたら不要？)
                mMap.clear();
                //マーカの再描画
                for (MarkerOptions mo : rd.getMarkerOptions()) {
                    mMap.addMarker(mo);
                }
                //マーカ同士の再接続
                if (rd.getCurrentIndex() < 2) {
                    lastPolyLine = null;
                } else {
                    lastPolyLine = mMap.addPolyline(rd.getPolylineOptions());
                }
                //最後までredoしたら，ボタンを利用不可にしている
                if(rd.getCurrentIndex() == rd.getLatLngSize()){
                    redo.setEnabled(false);
                }
                //undoボタンを利用可能にする
                undo.setEnabled(true);
            }
        });



        //rd = new ReviewData("Dummy ID");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
        Log.d("Timer", "onDestroy");
    }

    //マップ関連
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


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

        //タップされた位置の取得
        if (mMap != null) {
            mMap.setOnMapClickListener(new OnMapClickListener() {
                MarkerOptions options = new MarkerOptions();
                @Override
                public void onMapClick(LatLng point) {
                    //タップ後にundoのボタンを利用可能にする
                    undo.setEnabled(true);
                    //undo後にタップしたら，redoできないようにする．
                    redo.setEnabled(false);
                    //座標情報をReviewDataへ登録
                    rd.addLatLng(point);

                    //マーカを地図上に描画
                    for(MarkerOptions mo : rd.getMarkerOptions()){
                        mMap.addMarker(mo);
                    }
                    //選択した地点を線で結ぶ
                    PolylineOptions polyOptions = rd.getPolylineOptions();
                    if(polyOptions != null) {
                        //lastPolyLineは，一番最後に描画した直線
                        //これを保存しておくことで，undoで直線を削除できる
                        lastPolyLine = mMap.addPolyline(polyOptions);
                    }
                }
            });
        }
    }
}
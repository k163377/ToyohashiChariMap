package com.doubutsunoyakata.toyohashicharimap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.doubutsunoyakata.toyohashicharimap.input.MapsActivity;

public class MainActivity extends AppCompatActivity {
    Button input;
    Button serch;
    Button history;
    Button upload;

    //GPSの権限を要求し、許可されたらMapへ移る
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: { //requestPermissions()の第2引数で指定した値
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//許可された場合
                    Intent intent[] = new Intent[1];
                    intent[0] = new Intent(MainActivity.this, MapsActivity.class);
                    startActivities(intent);
                }else{//拒否された場合の処理
                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (Button) findViewById(R.id.Input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//GPSからの入力への移行
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {//権限がまだ無い場合
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {//明示的に権限が拒否されていた時
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {//まだ聞いてなかったとき
                        //与えても良いか聞く、onRequestPermissionsResultが答えを受ける
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }else{
                    Intent intent[] = new Intent[1];
                    intent[0] = new Intent(MainActivity.this, MapsActivity.class);
                    startActivities(intent);
                }
            }
        });

        serch = (Button) findViewById(R.id.Serch);

        upload = (Button) findViewById(R.id.Upload);

        history = (Button) findViewById(R.id.History);
    }
}

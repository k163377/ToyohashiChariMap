package com.doubutsunoyakata.toyohashicharimap.input;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public final class ReviewData {
    private final String dataID;//レビューデータの識別子、データを格納するファイル名としても利用する予定
    private final ArrayList<LatLng> latLngArray;//座標の行列
    private int currentIndex;//入力の取り消し機能のため、今読んでいる場所を記憶]
    private double time;//経過時間の計測

    //プログラムから呼び出す方のコンストラクタ
    public ReviewData(String ID){
        this.dataID = ID;
        latLngArray = new ArrayList<LatLng>();

        currentIndex = 0;
    }
    //ファイルから読み取る場合のコンストラクタ
    /*まだ作ってない、後回しで*/

    //ゲッター
    public ArrayList<LatLng> getLatLngArray(){
        return latLngArray;
    }
    public PolylineOptions getPolylineOptions(){
        //無いならnullで返す
        if(currentIndex < 2) return null;
        //現在地まででリターン
        PolylineOptions plo = new PolylineOptions();
        for(int i = 0; i < currentIndex; i++) plo.add(latLngArray.get(i));
        //デバッグ
        System.out.println(plo.toString());
        return plo;
    }
    public int getCurrentIndex(){ return currentIndex; }

    //セッター
    public void addLatLng(LatLng p){
        latLngArray.add(p);
        currentIndex++;
    }

    //インデックスのアンドゥ
    public void undoCurrentIndex(){ if(0 < currentIndex) currentIndex--; }
    //インデックスのリドゥ
    public void redoCurrentIndex(){ if(currentIndex < latLngArray.size()) currentIndex++; }

    //シリアライズ、まだ
}

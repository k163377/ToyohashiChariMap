package com.doubutsunoyakata.toyohashicharimap.input;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.app.Activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public final class ReviewData implements Serializable{
    private static final long serialVersionUID = 6255752248513019207L;

    private final String dataID;//レビューデータの識別子、データを格納するファイル名としても利用する予定
    private final ArrayList<LatLng> latLngArray;//座標の行列
    private final ArrayList<MarkerOptions> markerArray;//マーカーのリスト
    private int currentIndex;//入力の取り消し機能のため、今読んでいる場所を記憶
    private Date date;  // レビュー作成時の時刻
    private String review = null; //レビューの内容

    private static final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//時刻用フォーマット

    //プログラムから呼び出す方のコンストラクタ
    public ReviewData(String name){
        latLngArray = new ArrayList<LatLng>();
        markerArray = new ArrayList<MarkerOptions>();
        currentIndex = 0;
        date = new Date( System.currentTimeMillis());//現在時間を取得
        dataID = name + "_" + df.format(date);
    }
    /**
     * ファイルからデシリアライズして読み取るコンストラクタ
     * @param activity 呼び出しもと
     * @param dataID 識別子
     */
    public ReviewData(final Activity activity, final String dataID) {
        ReviewData rd = inputDeserialize( activity, dataID);
        this.dataID = rd.dataID;
        this.latLngArray = rd.latLngArray;
        this.markerArray = rd.markerArray;
        this.currentIndex = rd.currentIndex;
        this.date = rd.date;
    }

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
    public ArrayList<MarkerOptions> getMarkerOptions(){
        if(currentIndex == 0) return null;
        ArrayList<MarkerOptions> mo = new ArrayList<MarkerOptions>();
        for(int i = 0; i < currentIndex; i++) mo.add(markerArray.get(i));
        return mo;
    }
    public int getCurrentIndex(){ return currentIndex; }
    public String getReview(){ return review; }

    //セッター
    public void addLatLng(LatLng p){
        MarkerOptions m = new MarkerOptions();
        m.position(p);
        latLngArray.add(currentIndex, p);
        markerArray.add(currentIndex++, m);
    }
    public void setReview(String r) { review = r; }

    //インデックスのアンドゥ
    public void undoCurrentIndex(){
        if(0 < currentIndex){
            currentIndex--;
        }
    }
    //インデックスのリドゥ
    public void redoCurrentIndex(){
        if(currentIndex < latLngArray.size()){
            currentIndex++;
        }
    }

    //latLngArrayのサイズ取得
    public int getLatLngSize(){
        return latLngArray.size();
    }

    /**
     * 自身をシリアライズしてファイルへと出力
     * @param activity 呼び出しもと
     * @return シリアライズ成功の可否
     */
    public boolean outputSerialize(final Activity activity) {
        try {
            // Androidアプリ用パス取得メソッド #Activity.class
            FileOutputStream fos = activity.openFileOutput( dataID + ".dat", MODE_PRIVATE);
            // オブジェクトのシリアライズ
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * ファイルをデシリアライズしてオブジェクトを取得
     * @param activity 呼び出しもと
     * @param dataID 識別子
     */
    public static ReviewData inputDeserialize(final Activity activity, final String dataID) {
        try {
            // Androidアプリ用パス取得メソッド #Activity.class
            FileInputStream fis = activity.openFileInput( dataID +".dat");
            // オブジェクトのデシリアライズ
            ObjectInputStream ois = new ObjectInputStream(fis);
            ReviewData rd = (ReviewData)ois.readObject();
            ois.close();
            if (dataID.equals(rd.dataID)) {
                return rd;
            }
            else
                throw new IOException();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
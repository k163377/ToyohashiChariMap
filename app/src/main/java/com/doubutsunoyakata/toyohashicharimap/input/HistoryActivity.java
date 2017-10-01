package com.doubutsunoyakata.toyohashicharimap.input;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.doubutsunoyakata.toyohashicharimap.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private File[] files;
    private List<String> dataList = new ArrayList<String>();
    private ListView lv;

    private String filePath;
//    private String filePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        filePath = this.getFilesDir().getPath();

        if(filePath != null) {
            files = new File(filePath).listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(".dat")) {
                    dataList.add(files[i].getName());
                    System.out.println(files[i].getName());
                }
            }
        }
    }
}

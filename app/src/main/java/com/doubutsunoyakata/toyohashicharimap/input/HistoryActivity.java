package com.doubutsunoyakata.toyohashicharimap.input;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.doubutsunoyakata.toyohashicharimap.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private File[] files;
    public List<String> dataList = new ArrayList<String>();

    public HistoryActivity(){
//        getFilePath();
    }

    public List<String> getFileList(){
        return dataList;
    }

    private void getFilePath(){
        String filePath = this.getFilesDir().getPath();
        if(filePath != null) {
            files = new File(filePath).listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(".dat")) {
                    dataList.add(files[i].getName());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Button showAll;
        String filePath = this.getFilesDir().getPath();

        LinearLayout llVertical = new LinearLayout(this);
        llVertical.setOrientation(LinearLayout.VERTICAL);
        llVertical.setGravity(Gravity.CENTER_VERTICAL);

        if(filePath != null) {
            files = new File(filePath).listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(".dat")) {
                    dataList.add(files[i].getName());

                    TextView tv = new TextView(this);
//                    tv.setHeight(itemHeight);
//                    tv.setWidth(itemWidth);
                    tv.setGravity(Gravity.CENTER);
                    tv.setText(files[i].getName());
                    llVertical.addView(tv);

                    setContentView(llVertical);
//                    System.out.println(files[i].getName());
                }
            }
        }

        showAll = (Button) findViewById(R.id.showAll);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent[] = new Intent[1];
                intent[0] = new Intent(HistoryActivity.this, HistoryMapActivity.class);
                startActivities(intent);
            }
        });
    }
}

package com.example.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    OneThread oneThread;

    ArrayList<String> oddDatas;
    ArrayList<String> evenDatas;

    ArrayAdapter<String> oddAdapter;
    ArrayAdapter<String> evenAdapter;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView oddListView=(ListView)findViewById(R.id.lab3_list_odd);
        ListView evenListView=(ListView)findViewById(R.id.lab3_list_even);

        oddDatas=new ArrayList<>();
        evenDatas=new ArrayList<>();

        oddAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, oddDatas);
        evenAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, evenDatas);

        oddListView.setAdapter(oddAdapter);
        evenListView.setAdapter(evenAdapter);

        handler=new Handler();

        oneThread=new OneThread();
        oneThread.start();

        TwoThread twoThread=new TwoThread();
        twoThread.start();

    }

    class OneThread extends Thread {
        Handler oneHandler;

        @Override
        public void run() {
            Looper.prepare();
            oneHandler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    SystemClock.sleep(1000);
                    final int data=msg.arg1;
                    if(msg.what==0){
                        handler.post(new Runnable(){
                            @Override
                            public void run() {
                                evenDatas.add("even : "+data);
                                evenAdapter.notifyDataSetChanged();
                            }
                        });
                    }else if(msg.what==1){
                        handler.post(new Runnable(){
                            @Override
                            public void run() {
                                oddDatas.add("odd : "+data);
                                oddAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            };
            Looper.loop();
        }
    }

    class TwoThread extends Thread {
        @Override
        public void run() {
            Random random=new Random();
            for(int i=0; i<10; i++){
                SystemClock.sleep(100);
                int data=random.nextInt(10);
                Message message=new Message();
                if(data % 2 == 0){
                    message.what=0;
                }else {
                    message.what=1;
                }
                message.arg1=data;
                message.arg2=i;
                oneThread.oneHandler.sendMessage(message);

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oneThread.oneHandler.getLooper().quit();
    }
}

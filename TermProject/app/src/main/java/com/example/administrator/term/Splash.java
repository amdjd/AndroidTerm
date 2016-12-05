package com.example.administrator.term;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler(){
            public void handleMessage(Message msg){
                finish();
            }
        };
        hd.sendEmptyMessageDelayed(0 , 3000); // 3초 후에 hd Handler 실행
        //트윈 애니메이션 사용
        ImageView myear = (ImageView) findViewById(R.id.year);
        Animation rocket_anim = AnimationUtils.loadAnimation(this, R.anim.year);
        myear.startAnimation(rocket_anim);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                startActivity(new Intent(getApplication(), MainActivity.class));
            }
        }, 3000);

    }
}

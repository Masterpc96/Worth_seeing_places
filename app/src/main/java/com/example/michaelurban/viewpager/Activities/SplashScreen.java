package com.example.michaelurban.viewpager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.michaelurban.viewpager.Activities.Activities.MainActivity;
import com.example.michaelurban.viewpager.R;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // create new thread
        Splash run = new Splash();
        // run created thread
        run.start();
    }

    private class Splash extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // create new intention to start new activity
            Intent intent = new Intent(SplashScreen.this,MainActivity.class);

            // start new activity
            startActivity(intent);

            // close Splash screen activity
            SplashScreen.this.finish();
        }
    }
}

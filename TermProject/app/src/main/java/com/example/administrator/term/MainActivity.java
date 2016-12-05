package com.example.administrator.term;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final MonthFragment MonthFragment = new MonthFragment();
    final WeekFragment WeekFragment = new WeekFragment();
    final DayFragment DayFragment = new DayFragment();
    final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, Splash.class));
        Handler mHandler = new Handler();

        mHandler.postDelayed(new Runnable()  {
            public void run() {
                checkDangerousPermissions();
            }
        }, 3000);
        fragmentTransaction.replace(R.id.fragment, new MonthFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_schedule:
                startActivity(new Intent(this, Add_scheduleActivity.class));
                return true;
            case R.id.month_schedule:
                switchFragment(0);
                return true;
            case R.id.week_schedule:
                switchFragment(1);
                return true;
            case R.id.day_schedule:
                switchFragment(2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void switchFragment(int id) {
        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (id == 0)
            fragmentTransaction.replace(R.id.fragment, MonthFragment);
        else if(id == 1)
            fragmentTransaction.replace(R.id.fragment, WeekFragment);
        else
            fragmentTransaction.replace(R.id.fragment, DayFragment);
        fragmentTransaction.commit();
    }
    void requestPermission() {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }
}

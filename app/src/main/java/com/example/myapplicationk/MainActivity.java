package com.example.myapplicationk;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothSPP bt;
    SecondFloor secondFloor;
    FirstFloor firstFloor;
    BatteryFragment batteryFragment;

    DrawerLayout drawer;
    Toolbar toolbar;

    Button f1Btn;
    Button f2Btn;

    ImageView drinks_pink;
    ImageView drinks_brown;

    Animation animComeInBW;
    Animation animMoveOutBW;
    Animation animComeInPN;
    Animation animMoveOutPN;

    Handler handler = new Handler();
    boolean isBrownCentered = false;

    TextView ch11;
    TextView ch13;
    TextView testBox;

    float weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ch11 = findViewById(R.id.ch11);
        ch13 = findViewById(R.id.ch13);
        testBox = findViewById(R.id.testBox);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firstFloor = (FirstFloor) getSupportFragmentManager().findFragmentById(R.id.container);
        batteryFragment = new BatteryFragment();
        secondFloor = new SecondFloor();

        drinks_pink = findViewById(R.id.drinks_pink);
        drinks_brown = findViewById(R.id.drinks_brown);
        animComeInBW = AnimationUtils.loadAnimation(this, R.anim.sliding_comeinbw);
        animMoveOutBW = AnimationUtils.loadAnimation(this, R.anim.sliding_moveoutbw);
        animComeInPN = AnimationUtils.loadAnimation(this, R.anim.sliding_comeinpn);
        animMoveOutPN = AnimationUtils.loadAnimation(this, R.anim.sliding_moveoutpn);

        SlidingThread slidingThread = new SlidingThread();
        slidingThread.start();

        bt = new BluetoothSPP(this); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });
        f1Btn = findViewById(R.id.f1button);
        f2Btn = findViewById(R.id.f2button);
        f1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f1Btn.setBackgroundResource(R.drawable.buttontaped);
                f2Btn.setBackgroundResource(R.drawable.buttonuntaped);
                onFragmentChanged(1);
            }
        });

        f2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f2Btn.setBackgroundResource(R.drawable.buttontaped);
                f1Btn.setBackgroundResource(R.drawable.buttonuntaped);
                onFragmentChanged(2);
            }
        });

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) { //data = 10진표
                if (message != null) {
                    weight = Float.parseFloat(message);
                    if (weight > 5000 && weight < 15000) {
                        testBox.setText(": " + weight + " :");
                        if (weight > 10005) {
                            ch11.setBackgroundColor(Color.parseColor("#78dbff"));
                        } else {
                            ch11.setBackgroundColor(Color.parseColor("#00ffffff"));
                        }
                    }else if(weight>15000 && weight<25000){
                        testBox.setText(": " + weight + " :");
                        f1Btn.setText(": " + weight + " :");
                        if (weight > 20005) {
                            ch13.setBackgroundColor(Color.parseColor("#78dbff"));
                        } else {
                            ch13.setBackgroundColor(Color.parseColor("#00ffffff"));
                        }
                    }
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제

            }

            public void onDeviceConnectionFailed() { //연결실패

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            onFragmentSelected(0, null);
            f1Btn.setBackgroundColor(getColor(R.color.purple_200));
            f2Btn.setBackgroundColor(getColor(R.color.gray));
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_gallery) {  //블루투스메뉴
            onFragmentSelected(1, null);
        } else if (id == R.id.nav_slideshow) {  //배터리메뉴
            drawer.closeDrawer(GravityCompat.START);
            onFragmentSelected(2, null);
        }
        return true;
    }


    public void onFragmentSelected(int position, Bundle bundle) {
        Fragment curFragment = null;

        if (position == 0) {
            curFragment = firstFloor;

        } else if (position == 1) {
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if (position == 2) {
            curFragment = batteryFragment;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment).commit();
    }

    public void onFragmentChanged(int index) {
        if (index == 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, firstFloor).commit();
        } else if (index == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, secondFloor).commit();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                // setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    class SlidingThread extends Thread {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AnimationListenerBRW animationListenerBWN = new AnimationListenerBRW();
                    animComeInBW.setAnimationListener(animationListenerBWN);
                    drinks_brown.startAnimation(animComeInBW);
                    drinks_pink.startAnimation(animMoveOutPN);
                }
            });
        }
    }

    private class AnimationListenerBRW implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            AnimationListenerPNK animationListenerPNK = new AnimationListenerPNK();
            animComeInPN.setAnimationListener(animationListenerPNK);
            drinks_pink.startAnimation(animComeInPN);
            drinks_brown.startAnimation(animMoveOutBW);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private class AnimationListenerPNK implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            AnimationListenerBRW animationListenerBWN = new AnimationListenerBRW();
            animComeInBW.setAnimationListener(animationListenerBWN);
            drinks_brown.startAnimation(animComeInBW);
            drinks_pink.startAnimation(animMoveOutPN);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
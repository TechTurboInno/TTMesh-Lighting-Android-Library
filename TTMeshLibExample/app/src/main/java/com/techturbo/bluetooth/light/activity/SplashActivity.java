package com.techturbo.bluetooth.light.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;
import com.techturbo.bluetooth.light.TelinkLightApplication;
import com.techturbo.bluetooth.light.widget.LoadingView;
import com.techturbo.okhttputils.okhttp.model.MessageEvent;
import com.techturbo.okhttputils.okhttp.util.RealmHelper;
import com.techturbo.ttmeslight.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getName();
    private RealmHelper mRealmHelper;
    private LayoutInflater mInflater;
    private TelinkLightApplication mApplication;
    private SVProgressHUD mSVProgressHUD;
    private boolean showloginbutton;
    LoadingView lv;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mInflater = LayoutInflater.from(this);
        showloginbutton = this.getIntent().getBooleanExtra("showloginbutton", false);

        this.mApplication = (TelinkLightApplication) this.getApplication();
        this.mApplication.doInit();

        mRealmHelper=new RealmHelper();
        mSVProgressHUD = new SVProgressHUD(this);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener(){
            @Override
            public void onDismiss(SVProgressHUD hud) {
                // todo something, like: finish current activity
//                Toast.makeText(getApplicationContext(),"dismiss",Toast.LENGTH_SHORT).show();
            }
        });

        EventBus.getDefault().register(this);

        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoClose();
            }
        }, 1000);

    }

    public void autoClose(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)

    public void onEventMainThread(MessageEvent evnt)

    {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        Log.e(TAG, "onDestroy");
    }

}

package com.example.taozhiheng.dotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import lock.LocusPasswordView;
import util.Constant;
import util.Utils;

/**
 * Created by taozhiheng on 15-4-6.
 *
 */
public class LockActivity extends ActionBarActivity {


    private SharedPreferences mPref;

    //设置，或是解锁 0-设置 1-解锁
    private int mActionType;
    private boolean mIsOpen;
    private boolean mCanSet;
    private String mPassword;

    private Toolbar mToolbar;
    private SwitchCompat mSwitcher;
    private LocusPasswordView mLock;

    private int mErrorCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        mActionType = getIntent().getIntExtra(Constant.LOCK_ACTION_TYPE, Constant.LOCK_ACTION_OPEN);
        mPref = getSharedPreferences(Constant.SHARED_PREF, MODE_PRIVATE);
        mIsOpen = mPref.getBoolean(Constant.LOCK_IS_OPEN, false);
        //应用启动但未开启密码
        if(mActionType == Constant.LOCK_ACTION_OPEN && !mIsOpen) {
            finish();
            startActivity(new Intent(Constant.INTENT_ACTION_MAIN));
        }

        setContentView(R.layout.lock);
        mLock = (LocusPasswordView) findViewById(R.id.lock);
        mToolbar = (Toolbar) findViewById(R.id.lock_toolbar);
        mLock.setOnCompleteListener(mOnCompleteListener);

        mPassword = mPref.getString(Constant.LOCK_PASSWORD, null);
        mCanSet = (mPassword == null);

        if(mActionType == Constant.LOCK_ACTION_SET) {
            mToolbar.setTitle(mCanSet ? "设置新密码" : "确认原密码");
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.action_bar_back_dark);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);
                }
            });
        }
        else
            mToolbar.setTitle("手势解锁");


        mSwitcher = new SwitchCompat(this);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT,
                Gravity.RIGHT);
        mSwitcher.setLayoutParams(params);
        mToolbar.addView(mSwitcher);

        mSwitcher.setOnCheckedChangeListener(mOnCheckedChangeListener);

        //开启状态，只有在验证密码成功后才允许直接关闭
        if(mIsOpen) {
            mSwitcher.setChecked(true);
            mSwitcher.setEnabled(false);
        }
        //关闭状态，不允许设置密码，只有在打开后才可以设置密码
        else
        {
            mSwitcher.setChecked(false);
            mLock.disableTouch();
        }
    }


    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
                mLock.enableTouch();
            else
            {
                mPref.edit().putString(Constant.LOCK_PASSWORD, null).apply();
                mLock.disableTouch();
            }
            mIsOpen = isChecked;
            mPref.edit().putBoolean(Constant.LOCK_IS_OPEN, mIsOpen).apply();
        }
    };

    private LocusPasswordView.OnCompleteListener mOnCompleteListener = new LocusPasswordView.OnCompleteListener() {
        @Override
        public void onComplete(String password) {
            mLock.clearPassword(100);
            if(mActionType == Constant.LOCK_ACTION_SET)
                {
                    if(mPassword == null) {
                        mPassword = password;
                        mToolbar.setTitle("确认新密码");
                    }
                    else if(mPassword.equals(password))
                    {
                        if(mCanSet)
                        {
                            mPref.edit().putString(Constant.LOCK_PASSWORD, mPassword).apply();
                            Toast.makeText(getBaseContext(), "设置成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            mPassword = null;
                            mCanSet = true;
                            mSwitcher.setEnabled(true);
                            mToolbar.setTitle("设置新密码");
                        }
                    }
                    else {
                        mLock.markError();
                        String str = "密码不正确,请重试";
                        if(mCanSet)
                            str = "两次输入不一致,请重试";
                        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //解锁成功
                    if(mPassword.equals(password))
                    {
                        startActivity(new Intent(Constant.INTENT_ACTION_MAIN));
                        finish();
                    }
                    else
                    {
                        mLock.markError();
                        mErrorCount++;
                        if(mErrorCount >= 5)
                        {
                            Toast.makeText(getBaseContext(), "你已5次绘错，请在10秒后重试", Toast.LENGTH_SHORT).show();
                            mLock.setVisibility(View.GONE);
                            new MyWaitThread().start();
                        }
                        Toast.makeText(getBaseContext(), "抱歉,请重试", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    };

    class MyWaitThread extends Thread
    {
        @Override
        public void run() {
            try
            {
                Thread.sleep(10 * 1000);
                mHandler.sendEmptyMessage(0);
            }catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            mLock.setVisibility(View.VISIBLE);
            mErrorCount = 0;
        }
    };
}

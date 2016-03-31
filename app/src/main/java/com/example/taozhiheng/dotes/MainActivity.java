package com.example.taozhiheng.dotes;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import recycler.MyOnItemClickListener;
import recycler.MyOnItemLongClickListener;
import recycler.MyRecordAdapter;
import recycler.Record;
import slidingmenu.src.main.java.com.example.taozhiheng.slidingmenu.SlidingMenu;
import sql.DBOperate;
import ui.MyAnimation;
import ui.MyThemeAdapter;
import util.Constant;
import util.Utils;

/**
 * Created by taozhiheng on 15-3-26.
 *
 */
public class MainActivity extends ActionBarActivity{



    private DBOperate mDBOperate;

    private SlidingMenu mSlidingMenu;
    private Toolbar mToolbar;
    private EditText mCreateText;
    private AlertDialog mCreateDialog;
    private AlertDialog mDeleteDialog;
    private AlertDialog mThemeDialog;
    private RecyclerView mRecyclerView;
    private MyRecordAdapter mRecordAdapter;
    private List<Record> mRecordList;

    private Button[] functions = new Button[5];

    private Record mChoseRecord;

    private AlphaAnimation alphaAnimation;

    private ImageView shadow;

    private MenuItem[] mMenuItem;

    private static boolean shouldLoad;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBOperate.close();
    }

    @Override
    protected void onStop() {
        mRecyclerView.clearAnimation();
        shadow.clearAnimation();
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mMenuItem[1].isVisible()) {
            mMenuItem[0].setVisible(false);
            mMenuItem[1].setVisible(false);
            mRecordAdapter.setCheckState(false);
            mRecordAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.main);

        shadow = (ImageView) findViewById(R.id.shadow);

        mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding);

        functions[0] = (Button) findViewById(R.id.one);
        functions[1] = (Button) findViewById(R.id.two);
        functions[2] = (Button) findViewById(R.id.three);
        functions[3] = (Button) findViewById(R.id.four);
        functions[4] = (Button) findViewById(R.id.five);
        for(Button button : functions)
            button.setOnClickListener(mOnClickListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.setLogo(R.drawable.notebook);
        mToolbar.setTitle("Dotes");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.notebook);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggle();
            }
        });
        mDBOperate = DBOperate.getInstance(this);
        shouldLoad = true;
        init();
    }

    private void init()
    {
        alphaAnimation = new AlphaAnimation(1f, 0);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);

        View createView = getLayoutInflater().inflate(R.layout.create_dialog, null);
        mCreateText = (EditText)createView.findViewById(R.id.create_text);
        mCreateDialog = new AlertDialog.Builder(this)
                .setTitle("新建记录")
                .setView(createView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        {
                            String name = mCreateText.getText().toString();
                            long id = mDBOperate.insertRecord(name);
                            if (id != -1) {
                                mRecordList.add(new Record((int)id, name, 0, null, System.currentTimeMillis()));
                                mRecyclerView.scrollToPosition(mRecordAdapter.getItemCount() - 1);
                                mRecordAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        mDeleteDialog = new AlertDialog.Builder(this)
                .setTitle("删除")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mDBOperate.deleteRecord(mChoseRecord.getId())!=0)
                        {
                            mRecordList.remove(mChoseRecord);
                            mRecyclerView.scrollToPosition(mRecordAdapter.getItemCount() - 1);
                            mRecordAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();

        final SharedPreferences pref = getSharedPreferences(Constant.SHARED_PREF, MODE_PRIVATE);
        mThemeDialog = new AlertDialog.Builder(this)
                .setTitle("选择主题")
                .setSingleChoiceItems(new MyThemeAdapter(Constant.APPLICATION_THEME, Constant.APPLICATION_THEME_COLOR),
                        pref.getInt(Constant.APPLICATION_THEME_ID, 1), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.changeToTheme(MainActivity.this, which);
                                pref.edit().putInt(Constant.APPLICATION_THEME_ID, which).apply();
                            }
                        })
                .create();
    }

    @Override
    protected void onResume() {
        mRecyclerView.clearAnimation();
        shadow.clearAnimation();
        super.onResume();
        if(shouldLoad)
            load();
    }

    private void load()
    {
        Log.d("drag", "main load data");
        if((mRecordList=mDBOperate.getRecords()) == null)
            mRecordList = new ArrayList<>();
        mRecordAdapter = new MyRecordAdapter(mRecordList);
        mRecordAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecordAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
        mRecyclerView.setAdapter(mRecordAdapter);
        shouldLoad = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenuItem = new MenuItem[2];
        mMenuItem[0] = menu.findItem(R.id.action_main_delete);
        mMenuItem[1] = menu.findItem(R.id.action_main_cancel);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_main_create:
                mCreateDialog.show();
                break;
            case R.id.action_main_delete:
                List<Record> checkedRecordList = mRecordAdapter.getCheckedRecordList();
                int count = 0;
                for(Record record : checkedRecordList)
                {
                    if(record != null && mDBOperate.deleteRecord(record.getId())!=0)
                    {
                        mRecordList.remove(record);
                        count++;
                    }
                }
                mRecyclerView.scrollToPosition(mRecordAdapter.getItemCount() - count);
                //mRecordAdapter.notifyDataSetChanged();
            case R.id.action_main_cancel:
                mMenuItem[0].setVisible(false);
                mMenuItem[1].setVisible(false);
                mRecordAdapter.setCheckState(false);
                mRecordAdapter.notifyDataSetChanged();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap getBitmap(View view)
    {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    private MyOnItemClickListener mOnItemClickListener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view, Record record, int position) {
            mChoseRecord = record;

            Intent intent = new Intent();
            intent.setAction(Constant.INTENT_ACTION_DRAG);
            intent.putExtra(Constant.ITEM_RECORD, mChoseRecord);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

//            View icon = view.findViewById(R.id.grid_icon);
//            //shadow.layout(view.getLeft() + icon.getLeft(), mToolbar.getBottom() + view.getTop() + icon.getTop(), view.getLeft() + icon.getRight(), mToolbar.getBottom() + view.getTop() + icon.getBottom());
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)shadow.getLayoutParams();
//            layoutParams.setMargins(view.getLeft()+icon.getLeft(), view.getTop(), 0, 0);
//            shadow.setImageBitmap(getBitmap(icon));
//            shadow.setVisibility(View.VISIBLE);
//            shadow.invalidate();
//            Log.d("drag", "activity shadow position.x:" + shadow.getX() + " y:" + shadow.getY());
//            Animation animation = new MyAnimation(shadow, mRecyclerView.getWidth() / 2, mRecyclerView.getHeight() / 2+mToolbar.getHeight(),
//                    1.0f * mRecyclerView.getHeight() / view.getHeight());
//            animation.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                    mRecyclerView.startAnimation(alphaAnimation);
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//
////                    Intent intent = new Intent();
////                    intent.setAction(Constant.INTENT_ACTION_DRAG);
////                    intent.putExtra(Constant.ITEM_RECORD, mChoseRecord);
////                    startActivity(intent);
////                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
//                    shadow.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//            shadow.startAnimation(animation);
        }
    };

    private MyOnItemLongClickListener mOnItemLongClickListener = new MyOnItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, Record record, int position) {
            mChoseRecord = record;
            mRecordAdapter.setCheckState(true);
            mRecordAdapter.notifyDataSetChanged();
            Vibrator vib = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(500);
            mMenuItem[0].setVisible(true);
            mMenuItem[1].setVisible(true);
//            mDeleteDialog.setMessage("你确定要删除记录:'"+mChoseRecord.getName()+"'吗?");
//            mDeleteDialog.show();
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.one:
                    Intent intent = new Intent().setClass(MainActivity.this, LockActivity.class);
                    intent.putExtra(Constant.LOCK_ACTION_TYPE, 0);
                    startActivity(intent);
                    return;
                case R.id.two:
                    mThemeDialog.show();
                    break;
//                case R.id.three:
//                    startActivity(new Intent(MainActivity.this, FlowActivity.class));
//                    break;
//                case R.id.four:
//                    break;
//                case R.id.five:
//                    break;
            }
        }
    };

    public static void setShouldLoad(boolean should)
    {
        shouldLoad = should;
    }
}

package com.example.taozhiheng.dotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import drag.Shape;
import sql.DBOperate;
import ui.SwipeActivity;
import util.Constant;
import util.Utils;

/**
 * Created by taozhiheng on 15-3-26.
 *
 */
public class ContentActivity extends SwipeActivity {

    private Toolbar mToolbar;
    private EditText mContent;
    private Shape mCurrentShape;

    private int mState;

    private EditText mRenameText;
    private AlertDialog mRenameDialog;

    private DBOperate mDBOperate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        mToolbar = (Toolbar) findViewById(R.id.content_toolbar);
        mContent = (EditText) findViewById(R.id.content);
        mCurrentShape = getIntent().getParcelableExtra(Constant.ITEM_SHAPE);
        mToolbar.setTitle(mCurrentShape.getTitle() == null ? "(空)":mCurrentShape.getTitle());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.action_bar_back_dark);
        mState = getIntent().getIntExtra(Constant.RECORD_STATE, 0);
        mDBOperate = DBOperate.getInstance(this);
        init();
        showContent();
        if(mState == 3) {
            mContent.setEnabled(false);
        }
    }

    private void init()
    {
        View renameView = getLayoutInflater().inflate(R.layout.rename_dialog, null);
        mRenameText = (EditText)renameView.findViewById(R.id.rename_text);
        mRenameDialog = new AlertDialog.Builder(this)
                .setTitle("重命名")
                .setView(renameView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        {
                            mToolbar.setTitle(mRenameText.getText().toString());
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
    }


    @Override
    protected void onPause() {
        recordContent();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mState != 3)
            getMenuInflater().inflate(R.menu.menu_content, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_content_rename:
                mRenameText.setText(mToolbar.getTitle());
                mRenameText.requestFocus();
                mRenameDialog.show();
                return true;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showContent()
    {
        mContent.setText(mCurrentShape.getContent());
        mContent.requestFocus();
    }

    private void recordContent() {
        mCurrentShape.setTitle(mToolbar.getTitle().toString());
        mCurrentShape.setContent(mContent.getText().toString(), false);
        mDBOperate.updateShape(mCurrentShape);
    }
}

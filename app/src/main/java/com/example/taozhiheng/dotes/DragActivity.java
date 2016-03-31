package com.example.taozhiheng.dotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import drag.Shape;
import drag.DragView;
import drag.OnShapeClickListener;
import drag.OnShapeLongClickListener;
import drag.ShapeCreator;
import recycler.Record;
import sql.DBOperate;
import util.Constant;
import util.Utils;


public class DragActivity extends ActionBarActivity {




    private String msg = "drag";

    private Toolbar mToolbar;
    private DragView dragView;

    private EditText mRenameText;
    private AlertDialog mRenameDialog;

    private DBOperate mDBOperate;
    private Record mCurrentRecord;
    private Shape mChoseShape;

    private MenuItem mMenuItem;
    private int mType;
    private int index;

    private float stopX;
    private float stopY;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("drag", "onSave");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.setShouldLoad(true);
        mDBOperate.updateShapes(dragView.getShapeList());
        Log.v("write stop:", "centerX:" + dragView.getShape(0).getCenterX());
        if(mCurrentRecord.getState() >= 2 && dragView.getShape(0).getCenterX() != stopX)
            dragView.performMove(stopX - dragView.getShape(0).getCenterX(), stopY - dragView.getShape(0).getCenterY());
        recordImage();
        Log.d("drag", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("drag", "onStop");
    }


    private void recordImage()
    {
        File file = new File(Environment.getExternalStorageDirectory()+ File.separator+"Dotes");
        if(!file.exists() || file.isFile()) {
            if(!file.mkdir())
                return;
        }
        String path = Environment.getExternalStorageDirectory()+ File.separator+"Dotes"+File.separator+mCurrentRecord.getId()+".png";
        try {
            takeScreenShot(dragView, path);
            mCurrentRecord.setImagePath(path);
            mDBOperate.updateRecord(mCurrentRecord);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void takeScreenShot(View view, String path ) throws IOException{
        view.setDrawingCacheEnabled( true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        FileOutputStream out = new FileOutputStream(path);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag);
        dragView = (DragView) findViewById(R.id.drag);

        mCurrentRecord = getIntent().getParcelableExtra(Constant.ITEM_RECORD);

        mToolbar = (Toolbar) findViewById(R.id.drag_toolbar);
        mToolbar.setTitle(mCurrentRecord.getName());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.action_bar_back_dark);

        mDBOperate = DBOperate.getInstance(this);
        mType = ShapeCreator.CIRCLE;
        init();
    }

    private void init()
    {
        dragView.setOnShapeClickListener(new OnShapeClickListener() {
            @Override
            public void onClick(Shape shape, int index) {
                mChoseShape = dragView.getShape(index);
                Intent intent = new Intent();
                intent.setAction(Constant.INTENT_ACTION_CONTENT);
                intent.putExtra(Constant.ITEM_SHAPE, mChoseShape);
                intent.putExtra(Constant.RECORD_STATE, mCurrentRecord.getState());
                startActivity(intent);
            }
        });
        dragView.setOnShapeLongClickListener(new OnShapeLongClickListener() {
            @Override
            public void onLongClick(Shape shape, int index) {
                if (index == 0) {
                    if (dragView.fixStopShape()) {
                        Toast.makeText(getBaseContext(), "终点已锁定", Toast.LENGTH_SHORT).show();
                        mCurrentRecord.setState(2);
                        stopX = dragView.getShape(0).getCenterX();
                        stopY = dragView.getShape(0).getCenterY();
                        mDBOperate.updateRecord(mCurrentRecord);
                    }
                } else if (index == 1) {
                    if (dragView.fixStartShape()) {
                        Toast.makeText(getBaseContext(), "起点已锁定", Toast.LENGTH_SHORT).show();
                        mCurrentRecord.setState(1);
                        mDBOperate.updateRecord(mCurrentRecord);
                    }
                }
            }
        });
        dragView.setTextVisible(true);
        View renameView = getLayoutInflater().inflate(R.layout.rename_dialog, null);
        mRenameText = (EditText)renameView.findViewById(R.id.rename_text);
        mRenameDialog = new AlertDialog.Builder(this)
                .setTitle("重命名")
                .setView(renameView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        {
                            mCurrentRecord.setName(mRenameText.getText().toString());
                            mToolbar.setTitle(mCurrentRecord.getName());
                            mDBOperate.updateRecord(mCurrentRecord);
                            Toast.makeText(getBaseContext(), "重命名完成", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load()
    {
        List<Shape> shapeList = mDBOperate.getShapes(mCurrentRecord.getId());
        if(shapeList != null && shapeList.size()>=2)
        {
            dragView.initShapeList(shapeList);
            Log.d("drag", "state:"+mCurrentRecord.getState());
            switch(mCurrentRecord.getState())
            {
                case 1:
                    dragView.fixStartShape();
                    break;
                case 2:
                    dragView.fixStartShape();
                    dragView.fixStopShape();
                    break;
                case 3:
                    dragView.fixStartShape();
                    dragView.fixStopShape();
                    dragView.complete();
                    break;
            }
            stopX = shapeList.get(0).getCenterX();
            stopY = shapeList.get(0).getCenterY();
        }
        else
        {
            shapeList = new ArrayList<>();
            long id = mDBOperate.insertShape(mCurrentRecord.getId(), 0,
                    dragView.getRightLimit()- 2*Shape.DEFAULT_RADIUS, dragView.getTopLimit()+2*Shape.DEFAULT_RADIUS, Shape.DEFAULT_RADIUS,
                    "stop", null, mType);
            shapeList.add(ShapeCreator.createShape((int)id, mCurrentRecord.getId(), 0,
                    dragView.getRightLimit()- 2*Shape.DEFAULT_RADIUS, dragView.getTopLimit()+2*Shape.DEFAULT_RADIUS, Shape.DEFAULT_RADIUS,
                    "stop", null, mType));
            id = mDBOperate.insertShape(mCurrentRecord.getId(), 1,
                    dragView.getLeftLimit()+2*Shape.DEFAULT_RADIUS, dragView.getBottomLimit()-2*Shape.DEFAULT_RADIUS, Shape.DEFAULT_RADIUS,
                    "start", null, mType);
            shapeList.add(ShapeCreator.createShape((int)id, mCurrentRecord.getId(), 1,
                    dragView.getLeftLimit()+2*Shape.DEFAULT_RADIUS, dragView.getBottomLimit()-2*Shape.DEFAULT_RADIUS, Shape.DEFAULT_RADIUS,
                    "start", null, mType));
            dragView.initShapeList(shapeList);
        }
        index = shapeList.size();
    }


    private boolean add()
    {
        Shape shape = dragView.getShape(index - 1);
        float x = shape.getCenterX();
        float y = shape.getCenterY();
        double degree = Math.random() * 2 * Math.PI;
        x += 2f * shape.getRadius() * Math.cos(degree);
        y += 2F * shape.getRadius() * Math.sin(degree);
        if(dragView.canAddShape(x, y)) {
            long id = mDBOperate.insertShape(mCurrentRecord.getId(), index, x, y, Shape.DEFAULT_RADIUS, null, null, mType);
            dragView.addShape(ShapeCreator.createShape((int) id, mCurrentRecord.getId(), index,
                    x, y, Shape.DEFAULT_RADIUS, null, null, mType));
            index++;
            return true;
        }
        return false;
    }

    private boolean remove()
    {
        if(dragView.canRemoveShape()) {
            mDBOperate.deleteShape(dragView.getShape(index - 1).getId());
            dragView.removeShape();
            index--;
            return true;
        }
        return false;
    }

    private void rename()
    {
        mRenameText.setText(mCurrentRecord.getName());
        mRenameText.requestFocus();
        mRenameDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drag, menu);
        mMenuItem = menu.findItem(R.id.action_drag_shape);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_drag_add:
                if(!add()) {
                    String str = "请先长按以锁定起点,终点";
                    if(dragView.getFinishState())
                        str = "您的目标已完成,无须添加";
                    Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_drag_remove:
                if(!remove()) {
                    String str = "没有可移除的点";
                    if(dragView.getFinishState())
                        str = "您的目标已完成,不可移除";
                    Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_drag_complete:
                if(dragView.complete()) {
                    mCurrentRecord.setState(3);
                    mDBOperate.updateRecord(mCurrentRecord);
                }
                return true;
            case R.id.action_drag_rename:
                rename();
                return true;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);
                return true;
            case R.id.action_drag_shape_circle:
                if(mType != ShapeCreator.CIRCLE) {
                    mType = ShapeCreator.CIRCLE;
                    mMenuItem.setIcon(R.drawable.circle);
                }
                return true;
            case R.id.action_drag_shape_star:
                if(mType != ShapeCreator.STAR) {
                    mType = ShapeCreator.STAR;
                    mMenuItem.setIcon(R.drawable.star);
                }
                return true;
            case R.id.action_drag_shape_heart:
                if(mType != ShapeCreator.HEART) {
                    mType = ShapeCreator.HEART;
                    mMenuItem.setIcon(R.drawable.heart);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);

    }

}

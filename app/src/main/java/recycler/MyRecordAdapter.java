package recycler;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taozhiheng.dotes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taozhiheng on 15-4-1.
 *
 */
public class MyRecordAdapter extends RecyclerView.Adapter<MyRecordAdapter.ViewHolder> {

    private List<Record> mRecordList;
    private MyOnItemClickListener mOnItemClickListener;
    private MyOnItemLongClickListener mOnItemLongClickListener;
    private boolean mChecked;
    private List<Record> mCheckedRecordList;


    public MyRecordAdapter(List<Record> records)
    {
        this.mRecordList = records;
        mChecked = false;
        mCheckedRecordList = new ArrayList<>();
    }

    @Override
    public MyRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyRecordAdapter.ViewHolder viewHolder, int i) {
        Record record = mRecordList.get(i);
        Log.d("drag", "title:" + record.getName());
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnClickListener(mOnClickListener);
        viewHolder.itemView.setOnLongClickListener(mOnLongClickListener);
        viewHolder.name.setText(record.getName());
        viewHolder.time.setText("创建于:"+record.getCreateTime());
        viewHolder.checkBox.setTag(i);
        viewHolder.checkBox.setVisibility(mChecked ? View.VISIBLE : View.GONE);
        viewHolder.checkBox.setChecked(false);
        viewHolder.checkBox.setOnCheckedChangeListener(mCheckedListener);
        if(record.getState() == 3)
            viewHolder.time.append("(已完成)");
        MyAsyncTask task = new MyAsyncTask(viewHolder.icon);
        task.execute(record.getImagePath());
    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    public void setOnItemClickListener(MyOnItemClickListener onItemClickListener)
    {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(MyOnItemLongClickListener onItemLongClickListener)
    {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!mChecked && mOnItemClickListener != null) {
                Log.d("drag", "onClick");
                int position = (int)v.getTag();
                mOnItemClickListener.onItemClick(v, mRecordList.get(position), position);
            }
        }
    };

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(!mChecked && mOnItemLongClickListener != null) {
                Log.d("drag", "onLongClick");
                int position = (int)v.getTag();
                mOnItemLongClickListener.onItemLongClick(v, mRecordList.get(position), position);
            }
            return true;
        }
    };

    private CompoundButton.OnCheckedChangeListener mCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
                mCheckedRecordList.add(mRecordList.get((Integer) buttonView.getTag()));
            else
                mCheckedRecordList.remove(mRecordList.get((Integer) buttonView.getTag()));
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView icon;
        private TextView name;
        private TextView time;
        private TintCheckBox checkBox;

        public ViewHolder(View view)
        {
            super(view);
            this.name = (TextView) view.findViewById(R.id.grid_name);
            this.time = (TextView) view.findViewById(R.id.grid_time);
            this.icon = (ImageView) view.findViewById(R.id.grid_icon);
            this.checkBox = (TintCheckBox) view.findViewById(R.id.grid_checkbox);
        }
    }

    class MyAsyncTask extends AsyncTask<String, Integer, Bitmap>
    {
        private ImageView imageView;
        public MyAsyncTask(ImageView view)
        {
            this.imageView = view;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String iconPath = params[0];
            Log.d("drag", "path:"+iconPath);
            return BitmapFactory.decodeFile(iconPath);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void setCheckState(boolean state)
    {
        if(mChecked == state)
            return;
        mChecked = state;
        if(!mChecked)
            mCheckedRecordList.clear();
    }

    public List<Record> getCheckedRecordList()
    {
        return mCheckedRecordList;
    }
}

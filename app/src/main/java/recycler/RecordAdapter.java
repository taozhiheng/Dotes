package recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.internal.widget.TintCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taozhiheng.dotes.R;

import java.util.List;

/**
 * Created by taozhiheng on 15-3-11.
 *
 */
public class RecordAdapter extends BaseAdapter {

    private Context mContext;
    private List<Record> mRecordList;
    private boolean checked = false;

    public RecordAdapter(Context context, List<Record> records)
    {
        this.mContext = context;
        this.mRecordList = records;
    }

    @Override
    public int getCount() {
        return mRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            //convertView = layoutInflater.inflate(R.layout.list_view_item, null);
            convertView = layoutInflater.inflate(R.layout.grid_view_item, null);
            viewHolder = new ViewHolder();
            //viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            //viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.name = (TextView) convertView.findViewById(R.id.grid_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.grid_time);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.grid_icon);
            viewHolder.checkBox = (TintCheckBox) convertView.findViewById(R.id.grid_checkbox);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Record record = (Record) getItem(position);
        Log.d("drag", "title:"+record.getName());
        viewHolder.name.setText(record.getName());
        viewHolder.time.setText("创建于:"+record.getCreateTime());
        if(record.getState() == 3)
            viewHolder.time.append("  (已完成)");
        viewHolder.checkBox.setVisibility(checked? View.VISIBLE : View.GONE);
        MyAsyncTask task = new MyAsyncTask(viewHolder.icon);
        task.execute(record.getImagePath());
        return convertView;
    }

    class ViewHolder
    {
        private ImageView icon;
        private TextView name;
        private TextView time;
        private TintCheckBox checkBox;
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
            if(bitmap != null)
                imageView.setImageBitmap(bitmap);
        }
    }
}

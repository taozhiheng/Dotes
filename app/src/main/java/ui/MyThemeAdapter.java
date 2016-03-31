package ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.taozhiheng.dotes.R;

/**
 * Created by taozhiheng on 15-4-12.
 *
 */
public class MyThemeAdapter extends BaseAdapter {

    private String[] mTheme;
    private int[] mThemeColor;

    public MyThemeAdapter(String[] theme, int[] themeColor)
    {
        this.mTheme = theme;
        this.mThemeColor = themeColor;
    }

    @Override
    public int getCount() {
        return mTheme.length;
    }

    @Override
    public Object getItem(int position) {
        return mTheme[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item, parent, false);
            viewHolder.theme = (CheckedTextView) convertView;
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.theme.setBackgroundResource(mThemeColor[position]);
        viewHolder.theme.setText(mTheme[position]);
        return convertView;
    }

    private class ViewHolder
    {
        private CheckedTextView theme;
    }
}

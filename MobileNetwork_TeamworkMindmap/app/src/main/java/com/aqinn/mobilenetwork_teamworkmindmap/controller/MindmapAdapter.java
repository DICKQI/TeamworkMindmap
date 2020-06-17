package com.aqinn.mobilenetwork_teamworkmindmap.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;

import java.util.List;

/**
 * @author Aqinn
 * @date 2020/6/14 11:54 PM
 */
public class MindmapAdapter extends BaseAdapter {

    private Context mContext;
    private List<Mindmap> mindmaps;

    public MindmapAdapter(Context mContext, List<Mindmap> mindmaps) {
        this.mContext = mContext;
        this.mindmaps = mindmaps;
    }

    @Override
    public int getCount() {
        return mindmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return mindmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.mindmap_item, parent, false);
            vh.tv_name = convertView.findViewById(R.id.tv_mm_name);
            vh.iv_img = convertView.findViewById(R.id.iv_thumb_img);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv_name.setText(position != 0 ? mindmaps.get(position).getName() : "新建思维导图");
        vh.iv_img.setImageResource(position == 0 ? R.drawable.add_mindmap : R.drawable.mindmap_item_icon);
        return convertView;
    }

    class ViewHolder {
        ImageView iv_img;
        TextView tv_name;
    }

}

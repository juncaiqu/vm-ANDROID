package com.kdx.core.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kdx.core.R;
import com.kdx.core.ov.StateInfo;


public class MaintainAdapter extends BaseAdapter {
	  
    private List<StateInfo> dateList; //数据
    private Context context;
    private LayoutInflater mInflater;

    public MaintainAdapter(List<StateInfo> dateList, Context context) {
		this.dateList = dateList;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}
	public void setDateList(List<StateInfo> dateList){
    	this.dateList = dateList;
	}
    /* 
     * 获得数据总数 
     * */  
    @Override  
    public int getCount() {  
        return this.dateList.size();
    }  
   
	/* 
     * 根据索引为position的数据 
     * */  
    @Override  
    public StateInfo getItem(int position) {
        return dateList.get(position);
    }  
    /* 
     * 根据索引值获得Item的Id 
     * */  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.t_key = (TextView) convertView.findViewById(R.id.Key);
			viewHolder.t_value= (TextView) convertView.findViewById(R.id.Value);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.t_key.setText(dateList.get(position).getStateName());
		viewHolder.t_value.setText(dateList.get(position).getStateValue());
		return convertView;
	}  
	
	static class ViewHolder {
		TextView t_key;
		TextView t_value;
	}
}  
package com.scut.vc.xflib;

import java.util.ArrayList;

import com.scut.vc.ui.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ChatAdapter implements ListAdapter{
	
	private ArrayList<ChatEng> list;
	private Context context;
	public ChatAdapter(Context context, ArrayList<ChatEng> list){
		this.context = context;
		this.list = list;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChatEng ce = list.get(position);
		int itemLayout = ce.getLayoutID();
		
		LinearLayout layout = new LinearLayout(context);
		LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi.inflate(itemLayout, layout, true);
		
		TextView txvText = (TextView) layout.findViewById(R.id.txvInfo);
		txvText.setText(ce.getInfo());
		
		return layout;
	}
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	

	
}

package com.zhi.app.myview;

import java.util.ArrayList;

import com.zhi.app.model.WordButton;
import com.zhi.app.ui.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

/**
 * 自定义GridView控件
 * @author lenovo
 *
 */
public class MyGridView extends GridView {
	
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	private Context mContext;
	private LayoutInflater mInflater;
	private GridAdapter mAdapter;
	
	private Animation mScaleAnim; // 文字Button缩放动画

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		
		// 给自定义GridView设定数据适配器
		mAdapter = new GridAdapter();
		this.setAdapter(mAdapter);
	}
	
	/**
	 * 更新数据
	 * @param list
	 */
	public void updateData(ArrayList<WordButton> list) {
		this.mArrayList = list;
		// 通知适配器数据集已经改变了，刷新界面
		this.mAdapter.notifyDataSetChanged();
	}
	
	class GridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			WordButton viewHolder;
			
			if(convertView == null) {
				
				// 加载GridView中每一个Item的布局样式
				convertView = mInflater.inflate(R.layout.my_gridview_item, null);
				
				mScaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.word_scale);
				mScaleAnim.setStartOffset(position * 200);
				
				viewHolder = mArrayList.get(position);
				viewHolder.wordIndex = position;
				viewHolder.viewButton = (Button) convertView.findViewById(R.id.btn_item);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (WordButton) convertView.getTag();
			}
			
			// 给自定义文字显示Button设定显示文字
			viewHolder.viewButton.setText(viewHolder.wordText);
			
			// 给convertView设置动画
			convertView.startAnimation(mScaleAnim);
			
			return convertView;
		}
	}
}

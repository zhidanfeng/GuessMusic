package com.zhi.app.myview;

import java.util.ArrayList;

import com.zhi.app.inter.IWordButtonClickListener;
import com.zhi.app.model.WordButton;
import com.zhi.app.ui.R;
import com.zhi.app.util.MyLog;

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
 * 
 * @author zhidf
 *
 */
public class MyGridView extends GridView {

	/** 底部待选文字按钮的个数 */
	public static final int WORD_COUNT = 24;

	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	private Context mContext;
	private LayoutInflater mInflater;
	private GridAdapter mAdapter;

	private Animation mScaleAnim; // 文字Button缩放动画

	private IWordButtonClickListener mWordButtonClickListener;

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
	 * 
	 * @param list
	 */
	public void updateData(ArrayList<WordButton> list) {
		this.mArrayList = list;
		// 通知适配器数据集已经改变了，刷新界面
		//mod by zhidf 2015.6.23 修改GridView刷新方式
		// 不能使用notifyDataSetChanged，因为每一关的数据都不一样，所以需要给每一关重新设置数据源
		//this.mAdapter.notifyDataSetChanged();
		setAdapter(mAdapter);
	}

	/**
	 * 注册文字按钮点击事件
	 * 
	 * @param listener
	 */
	public void setOnWordButtonClickListener(IWordButtonClickListener listener) {
		this.mWordButtonClickListener = listener;
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

			final WordButton viewHolder;
			

			if (convertView == null) {

				// 加载GridView中每一个Item的布局样式
				convertView = mInflater.inflate(R.layout.my_gridview_item, null);

				mScaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.word_scale);
				// 设置动画的开始时间。这样每一个按钮之间的启动动画的间隔时间是200ms，看起来是当第一按钮动画执行完成
				// 后，后面一个按钮的动画才开始
				mScaleAnim.setStartOffset(position * 200);

				viewHolder = mArrayList.get(position);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (WordButton) convertView.getTag();
			}

			// add by zhidf 2015.6.18 增加viewButton的为空判断，修复点击第一个按钮不能隐藏的问题
			// 之所以出现这个问题是因为系统不知道该给Item绘制多高，它会先取第一个Item来试探以确定item绘制的具体高度
			// ，这样就导致多调用了一次getView方法，所以需要在程序中规避多次执行的position=0的操作
			// 在这个程序中使用了viewHolder.viewButton==null来规避多次执行的getView，因为当第一次position=0执行的时候
			// viewButton被实例化了，当后面的多次position=0执行的时候就发现viewButton不为空了，就直接跳过了，不执行。
			if (viewHolder.viewButton == null) {
				MyLog.i("zdf", position + "");
				viewHolder.wordIndex = position;
				viewHolder.viewButton = (Button) convertView.findViewById(R.id.btn_item);
				// 给自定义文字显示Button设定显示文字
				viewHolder.viewButton.setText(viewHolder.wordText);

				viewHolder.viewButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mWordButtonClickListener.onWordButtonClick(viewHolder);
					}
				});
			}

			// 给convertView设置动画
			convertView.startAnimation(mScaleAnim);

			return convertView;
		}
	}
}

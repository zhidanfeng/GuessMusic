package com.zhi.app.ui;

import java.util.ArrayList;

import com.zhi.app.model.WordButton;
import com.zhi.app.myview.MyGridView;
import com.zhi.app.util.Utils;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	private ImageView mViewPan;
	private ImageView mViewPanBar;
	private ImageButton mStartPlay;
	private MyGridView mGridView;
	private LinearLayout ll_select_word_container;

	// add by zhidf 2015.6.6 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanInterpolator;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInInterpolator;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutInterpolator;
	// end

	private boolean isRunning = false; // 动画是否正在执行

	private ArrayList<WordButton> mHadSelectDataList; // 已选中的文字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 界面初始化
		this.initView();

		// 加载底部按钮显示数据
		this.initData();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// start 控件初始化
		this.mViewPan = (ImageView) this.findViewById(R.id.iv_pan);
		this.mViewPanBar = (ImageView) findViewById(R.id.iv_pan_bar);
		this.mStartPlay = (ImageButton) findViewById(R.id.btn_start_play);
		this.mGridView = (MyGridView) findViewById(R.id.gridView);
		this.ll_select_word_container = (LinearLayout) findViewById(R.id.ll_select_word_container);

		this.mStartPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 1、当点击播放按钮时，棍子开始进入唱片即执行棍子进入动画
				// 2、棍子进入唱盘后，唱盘开始执行滚动动画
				// 3、唱盘动画执行完成后，棍子回到一开始的位置，即执行棍子返回动画
				// 因此需要给这三种动画设置对应的动画监听器，在动画的不同状态执行对应的动画

				// 动画未执行，则开始执行动画，并将标记设定为“执行”状态
				if (!isRunning) {
					mViewPanBar.startAnimation(mBarInAnim);
					isRunning = true;
					mStartPlay.setVisibility(View.INVISIBLE);
				}
			}
		});
		// end

		// start 动画初始化
		// 唱片动画初始化
		this.mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		this.mPanInterpolator = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanInterpolator);
		mPanAnim.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 唱盘滚动动画执行完后执行棍子返回动画，这时棍子返回最初位置
				mViewPanBar.startAnimation(mBarOutAnim);
				// 动画执行完成后，设定运行状态为停止，并将播放按钮显示出来
				isRunning = false;
				mStartPlay.setVisibility(View.VISIBLE);
			}
		});

		// 棍子进入动画
		this.mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		this.mBarInInterpolator = new LinearInterpolator();
		mBarInAnim.setInterpolator(mBarInInterpolator);
		mBarInAnim.setFillAfter(true); // 当动画结束后停留在当前位置，而不是回到初始位置
		mBarInAnim.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 棍子进入动画执行完成后，开始执行唱盘滚动动画，唱盘开始滚动
				mViewPan.startAnimation(mPanAnim);
			}
		});

		// 棍子返回动画
		this.mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		this.mBarOutInterpolator = new LinearInterpolator();
		mBarOutAnim.setInterpolator(mBarOutInterpolator);
		mBarOutAnim.setFillAfter(true); // 当动画结束后停留在当前位置，而不是回到初始位置
		mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		// end

		initHadSelectWordView();
	}

	private void initData() {

		initSelectData();

		initHadSelectData();
	}

	/**
	 * 加载底部待选数据
	 */
	private void initSelectData() {
		ArrayList<WordButton> arrayList = new ArrayList<WordButton>();
		WordButton wordButton = null;

		for (int i = 0; i < 24; i++) {
			wordButton = new WordButton();
			wordButton.wordText = "枫";
			arrayList.add(wordButton);
		}

		this.mGridView.updateData(arrayList);
	}

	/**
	 * 加载已选文字
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initHadSelectData() {
		ArrayList<WordButton> arrayList = new ArrayList<WordButton>();

		for (int i = 0; i < 4; i++) {
			View view = Utils.getView(MainActivity.this, R.layout.my_gridview_item);

			WordButton holder = new WordButton();

			holder.viewButton = (Button) view.findViewById(R.id.btn_item);

			holder.isVisible = false;
			holder.viewButton.setTextColor(Color.RED);
			holder.viewButton.setBackgroundResource(R.drawable.game_wordblank);
			arrayList.add(holder);
		}

		return arrayList;
	}

	/**
	 * 加载已选文字界面
	 */
	private void initHadSelectWordView() {
		this.mHadSelectDataList = initHadSelectData();

		for (int i = 0; i < this.mHadSelectDataList.size(); i++) {
			WordButton wordButton = this.mHadSelectDataList.get(i);
			// 设置每一个加入到LinearLayout中的Button的大小
			LayoutParams params = new LayoutParams(80, 80);
			// 将Button加入至LinearLayout中
			ll_select_word_container.addView(wordButton.viewButton, params);
		}
	}
}

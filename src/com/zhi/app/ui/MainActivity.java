package com.zhi.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private ImageView mViewPan;
	private ImageView mViewPanBar;
	private ImageButton mStartPlay;

	// add by zhidf 2015.6.6 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanInterpolator;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInInterpolator;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutInterpolator;
	// end

	private boolean isRunning = false; // 动画是否正在执行

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 界面初始化
		this.initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// start 控件初始化
		this.mViewPan = (ImageView) this.findViewById(R.id.iv_pan);
		this.mViewPanBar = (ImageView) findViewById(R.id.iv_pan_bar);
		this.mStartPlay = (ImageButton) findViewById(R.id.btn_start_play);

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
	}
}

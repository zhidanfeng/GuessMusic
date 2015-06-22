package com.zhi.app.ui;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.zhi.app.data.Constant;
import com.zhi.app.inter.IWordButtonClickListener;
import com.zhi.app.model.Song;
import com.zhi.app.model.WordButton;
import com.zhi.app.myview.MyGridView;
import com.zhi.app.util.MyLog;
import com.zhi.app.util.Utils;

public class MainActivity extends Activity implements IWordButtonClickListener {

	private ImageView mViewPan; // 唱片
	private ImageView mViewPanBar; // 唱片旁边的那根棍子^-^
	private ImageButton mStartPlay; // 播放按钮
	private MyGridView mGridView; // 最下方的文字按钮布局
	private LinearLayout ll_select_word_container; // 待填充文字按钮容器
	private TextView tv_curr_coin; // 显示当前可用金币

	// add by zhidf 2015.6.6 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanInterpolator;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInInterpolator;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutInterpolator;
	// end

	private boolean isRunning = false; // 动画是否正在执行

	private ArrayList<WordButton> mWaitSelectDataList; // 待选文字集合
	private ArrayList<WordButton> mHadSelectDataList; // 已选中的文字

	private Song mCurrSong; // 当前关卡的歌曲
	private int mCurrStageIndex = 3; // 当前关卡的索引，即第几关

	// add by zhidf 2015.6.20 定义答案检测结果常量：正确、错误、缺失
	/** 答案正确 */
	private static final int ANSWER_IS_RIGHT = 1;
	/** 答案错误 */
	private static final int ANSWER_IS_WRONG = 2;
	/** 答案不完整 */
	private static final int ANSWER_IS_INCOMPLETE = 3;

	/** 当前金币数量 */
	private int mCurrCoinNum = Constant.AVAILABLE_COIN;

	// end

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

		this.tv_curr_coin = (TextView) findViewById(R.id.txt_bar_coins);
		this.tv_curr_coin.setText(this.mCurrCoinNum + "");

		// 设置文字按钮点击事件
		this.mGridView.setOnWordButtonClickListener(this);

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

		handleFloatDeleteBtn();

		handleFloatShowTipBtn();
	}

	private void initData() {

		initSelectData();

		initWaitGuessSongData();
	}

	/**
	 * 加载底部待选数据
	 */
	private void initSelectData() {
		mWaitSelectDataList = new ArrayList<WordButton>();
		WordButton wordButton = null;

		String[] randomSongWord = this.generateRandomSongWord();

		for (int i = 0; i < MyGridView.WORD_COUNT; i++) {
			wordButton = new WordButton();
			wordButton.wordText = randomSongWord[i];
			mWaitSelectDataList.add(wordButton);
		}

		this.mGridView.updateData(mWaitSelectDataList);
	}

	/**
	 * 加载待猜歌曲数据<br/>
	 * <p>
	 * 根据歌曲名称长度设置显示界面数据
	 * </p>
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWaitGuessSongData() {
		ArrayList<WordButton> arrayList = new ArrayList<WordButton>();

		// 根据当前关卡歌曲的长度动态设置待填充的选择框的个数
		for (int i = 0; i < this.mCurrSong.getSongNameLength(); i++) {
			View view = Utils.getView(MainActivity.this, R.layout.my_gridview_item);

			final WordButton holder = new WordButton();

			holder.viewButton = (Button) view.findViewById(R.id.btn_item);

			holder.isVisible = false;
			holder.viewButton.setTextColor(Color.WHITE);
			holder.viewButton.setBackgroundResource(R.drawable.game_wordblank);

			// 添加已选文字按钮的点击事件，用于选择错误之后清除已选择的文字
			holder.viewButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					clearSelectedPanel(holder);
				}
			});

			arrayList.add(holder);
		}

		return arrayList;
	}

	/**
	 * 加载当前关卡歌曲数据
	 */
	private Song loadCurrStageSongData(int stageIndex) {

		Song song = new Song();

		// 设置当前关卡的歌曲数据：歌曲名称、歌曲文件名称
		String[] currStageSongData = Constant.SONG_INFO[stageIndex];
		song.setSongName(currStageSongData[Constant.INDEX_SONG_NAME]);
		song.setSongFileName(currStageSongData[Constant.INDEX_FILE_NAME]);

		return song;
	}

	/**
	 * 加载已选文字界面<br/>
	 * <p>
	 * 根据当前歌曲的名称的个数来加载显示的方框数
	 * </p>
	 */
	private void initHadSelectWordView() {

		// 因为mCurrStageIndex设置了初值1，而数组下标从0开始，故需要-1
		this.mCurrSong = this.loadCurrStageSongData(this.mCurrStageIndex - 1);

		this.mHadSelectDataList = initWaitGuessSongData();

		// 将待选文字按钮布局放入事先放置好的LinearLayout容器中
		for (int i = 0; i < this.mHadSelectDataList.size(); i++) {
			WordButton wordButton = this.mHadSelectDataList.get(i);
			// 设置每一个加入到LinearLayout中的Button的大小
			LayoutParams params = new LayoutParams(80, 80);
			// 将Button加入至LinearLayout中
			ll_select_word_container.addView(wordButton.viewButton, params);
		}
	}

	/**
	 * 文字按钮点击后的实现方法，即文字按钮点击之后的所有后续逻辑将在此完成
	 */
	@Override
	public void onWordButtonClick(WordButton wordButton) {
		this.setWordToSelectedPanel(wordButton);

		// add by zhidf 2015.6.20 检查所填歌曲名称是否正确
		int answerFlag = this.checkTheAnswer();
		if (answerFlag == ANSWER_IS_RIGHT) {
			Toast.makeText(MainActivity.this, "正确", Toast.LENGTH_SHORT).show();
		} else if (answerFlag == ANSWER_IS_WRONG) {
			// Toast.makeText(MainActivity.this, "错误",
			// Toast.LENGTH_SHORT).show();
			shakeTheAnswer();
		} else if (answerFlag == ANSWER_IS_INCOMPLETE) {
			Toast.makeText(MainActivity.this, "不完整", Toast.LENGTH_SHORT).show();
		}
		// end
	}

	/**
	 * 点击待选文字按钮，将该文字填充至已选框区域
	 * 
	 * @param wordButton
	 */
	private void setWordToSelectedPanel(WordButton wordButton) {
		// 遍历已选文字列表，如果为空，说明该位置还没有填充文字，则可以将待选文字框中的文字放置在该位置上
		for (int i = 0; i < this.mHadSelectDataList.size(); i++) {
			WordButton word = this.mHadSelectDataList.get(i);
			if (word.wordText.trim() == "") {

				// 设置已选文字框中的文字可见
				word.viewButton.setText(wordButton.wordText);
				word.viewButton.setTextColor(Color.WHITE);
				word.isVisible = true;
				word.wordText = wordButton.wordText;
				// 设置已选文字的索引，可以用于取消文字选择
				word.wordIndex = wordButton.wordIndex;

				// 设置待选文字框中的已经选择的文字为隐藏
				wordButton.viewButton.setVisibility(View.INVISIBLE);
				wordButton.isVisible = false;

				break;
			}
		}
	}

	/**
	 * 清除已选文字框中的文字
	 * 
	 * @param wordButton
	 */
	private void clearSelectedPanel(WordButton wordButton) {
		// 1、清除已选框中的文字
		wordButton.isVisible = false;
		wordButton.viewButton.setText("");
		wordButton.wordText = "";

		// 2、显示待选框对应位置的文字按钮
		// 获取待清除的文字按钮的索引
		int index = wordButton.wordIndex;
		// 恢复指定待选文字框的显示
		this.mWaitSelectDataList.get(index).viewButton.setVisibility(View.VISIBLE);
	}

	/**
	 * 生成随机的、包含歌曲名字在内的汉字
	 * 
	 * @return
	 */
	private String[] generateRandomSongWord() {
		Random random = new Random();

		// 初始化存放随机汉字的数组
		String[] words = new String[MyGridView.WORD_COUNT];

		int songNameLength = this.mCurrSong.getSongNameLength();

		// 首先将歌曲的名称存入数组中，这时数组里面汉字是顺序的
		for (int i = 0; i < songNameLength; i++) {
			words[i] = this.mCurrSong.convertSongNameToArray()[i] + "";
		}

		// 给数组剩下的位置填充随机生成的汉字
		for (int i = songNameLength; i < MyGridView.WORD_COUNT; i++) {
			words[i] = Utils.generateRandomWord();
		}

		// 因为数组前面几个汉字包含了歌曲名称并且是顺序的，所以需要打乱数组中的汉字顺序
		// 首先从WORD_COUNT个汉字中随机抽取一个汉字与最后一个汉字交换，然后抽取一个汉字与倒数第二个汉字交换
		// 依次类推，直到第一个元素
		for (int i = MyGridView.WORD_COUNT - 1; i >= 0; i--) {
			// nextInt的范围是[0, value),
			int index = random.nextInt(i + 1);

			String temp = words[index];
			words[index] = words[i];
			words[i] = temp;
		}

		return words;
	}

	/**
	 * 检验当前已填歌曲名字是否正确
	 * 
	 * @return 歌曲名字检查标记：正确、错误、不完整
	 * @remark add by zhidf 2015.6.20
	 */
	private int checkTheAnswer() {

		// 1、遍历已选文字集合，如果有一个文字为空，说明答案还不完整
		for (int i = 0; i < this.mHadSelectDataList.size(); i++) {
			if (this.mHadSelectDataList.get(i).wordText.length() == 0) {
				return ANSWER_IS_INCOMPLETE;
			}
		}

		// 2、获取当前已填的文字，将其组合成一个完整的字符串：同+桌+的->同桌的
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.mHadSelectDataList.size(); i++) {
			sb.append(this.mHadSelectDataList.get(i).wordText);
		}

		// 3、将最终的已选文字与当前歌曲的名字相比较，如果能匹配说明答案正确，否则错误
		if (this.mCurrSong.getSongName().equals(sb.toString())) {
			return ANSWER_IS_RIGHT;
		} else {
			return ANSWER_IS_WRONG;
		}
	}

	/**
	 * 闪烁所填文字
	 */
	private void shakeTheAnswer() {

		// final Timer timer = new Timer();
		// TimerTask task = new TimerTask() {
		//
		// int timeFlag = 0;
		//
		// @Override
		// public void run() {
		// runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// MyLog.i("zdf", timeFlag + "");
		// if(++timeFlag > 10) {
		// timer.cancel();
		// return;
		// }
		//
		// for (int i = 0; i < mHadSelectDataList.size(); i++) {
		// //WordButton wordButton = mHadSelectDataList.get(i);
		//
		// MyLog.i("zdf", (timeFlag % 2 == 0) + ", " + ((timeFlag / 2 == 0) ?
		// Color.RED : Color.WHITE));
		// mHadSelectDataList.get(i).viewButton.setTextColor((timeFlag / 2 == 0)
		// ? Color.RED : Color.WHITE);
		// }
		// }
		// });
		// }
		// };
		// timer.schedule(task, 1, 1000);

		// 定时器相关
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;

			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (++mSpardTimes > 16) {
							return;
						}

						// 执行闪烁逻辑：交替显示红色和白色文字
						for (int i = 0; i < mHadSelectDataList.size(); i++) {
							mHadSelectDataList.get(i).viewButton.setTextColor(mChange ? Color.RED
									: Color.WHITE);
						}

						mChange = !mChange;
					}
				});
			}
		};

		Timer timer = new Timer();
		// 在1秒之后执行task，并且每隔150ms执行一次
		timer.schedule(task, 1, 150);
	}

	/**
	 * 处理删除一个错误答案的业务逻辑
	 */
	private void handleFloatDeleteBtn() {
		ImageButton delete_wrong_answer = (ImageButton) findViewById(R.id.ib_delete_wrong_answer);
		delete_wrong_answer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int pay_delete_coin = Utils.getIntegerValues(MainActivity.this,
						R.integer.pay_delete_word);
				// 1、扣除金币
				// 1.1 先判断是否有足够的金币，如果有则扣除并显示剩余金币，否则提示用户金币不足
				boolean flag = checkTheCoinIsEnough(-pay_delete_coin);
				if (flag) {
					// 2、隐藏一个错误的答案。PS：不能隐藏掉正确的答案
					WordButton hideWordButton = findIsNotAnswer();
					if (hideWordButton == null) {
						return;
					}

					hideWordButton.isVisible = false;
					hideWordButton.viewButton.setVisibility(View.INVISIBLE);

					handleTheCoinNum(-pay_delete_coin);
				} else {
					Toast.makeText(MainActivity.this, "不够了", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 找出不是答案的一个文字
	 * 
	 * @return
	 */
	private WordButton findIsNotAnswer() {

		Random random = new Random();

		while (true) {
			int index = random.nextInt(MyGridView.WORD_COUNT);
			WordButton wordButton = mWaitSelectDataList.get(index);
			if (wordButton.isVisible && !isRightAnswer(wordButton.wordText)) {
				MyLog.i("zdf", "if..." + !isRightAnswer(wordButton.wordText));
				return wordButton;
			} else {
				// 查找过程中会出现这么一种情况：恰好随机查找到的那个汉字就是答案文字中的一个，那么肯定是不能没有反应的，
				// 这个时候应该继续自动查找，直到找到一个不是答案的文字
				MyLog.i("zdf", "else..." + !isRightAnswer(wordButton.wordText));
				return findIsNotAnswer();
			}
		}
	}

	/**
	 * 判断文字是否是答案（例：“桌”是否是“同桌的你”中的一个）
	 * 
	 * @param word
	 *            使用随机函数挑选到的一个汉字
	 * @return 属于歌曲名中的一个则返回true，否则返回false
	 */
	private boolean isRightAnswer(String word) {
		// 将随机挑选的一个汉字与当前歌曲名称比较，如果是歌曲名字中的一个则返回true
		for (int i = 0; i < mCurrSong.getSongNameLength(); i++) {
			if (word.equals(mCurrSong.convertSongNameToArray()[i] + "")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查当前的金币数量是否足够
	 * 
	 * @param coinNum
	 * @return
	 */
	private boolean checkTheCoinIsEnough(int coinNum) {
		if ((mCurrCoinNum + coinNum) > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 处理金币数量加减、显示逻辑
	 * 
	 * @param coinNum
	 *            待增加、待减少的金币数量
	 */
	private void handleTheCoinNum(int coinNum) {
		mCurrCoinNum += coinNum;
		tv_curr_coin.setText(mCurrCoinNum + "");
	}

	/**
	 * 处理“提示答案”按钮的点击业务逻辑
	 */
	private void handleFloatShowTipBtn() {
		ImageButton showTipBtn = (ImageButton) findViewById(R.id.ib_showtip);
		showTipBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 遍历已选文字框，如果有一个是空的，则可以将找到的那个正确答案填在这个空位上，并且退出循环
				// 否则会将整个已选文字框全部填满
				for (int i = 0; i < mHadSelectDataList.size(); i++) {
					if (mHadSelectDataList.get(i).wordText.length() == 0) {
						// 调用之前编写的按钮点击方法来实现自动将提示答案填写在已选文字框中
						onWordButtonClick(findIsAnswer(i));

						int pay_tip_coin = Utils.getIntegerValues(MainActivity.this, R.integer.pay_tip_answer);
						if(checkTheCoinIsEnough(-pay_tip_coin)) {
							// 扣除提示正确答案所需花费的金币
							handleTheCoinNum(-pay_tip_coin);
						} else {
							Toast.makeText(MainActivity.this, "不够了", Toast.LENGTH_SHORT).show();
						}
						
						break;
					}
				}
			}
		});
	}

	/**
	 * 从待选文字框中找到对应位置的正确答案
	 * 
	 * @param index
	 *            已选文字框的空白位置的索引
	 * @return 对应索引在待选文字框中对应的文字按钮
	 */
	private WordButton findIsAnswer(int index) {
		WordButton temp = null;
		for (int i = 0; i < MyGridView.WORD_COUNT; i++) {
			temp = mWaitSelectDataList.get(i);
			if (temp.wordText.equals(mCurrSong.convertSongNameToArray()[index] + "")) {
				return temp;
			}
		}
		return null;
	}

	private void handleFloatShareBtn() {

	}
}

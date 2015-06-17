package com.zhi.app.ui;

import java.util.ArrayList;
import java.util.Random;

import com.zhi.app.data.SongData;
import com.zhi.app.inter.IWordButtonClickListener;
import com.zhi.app.model.Song;
import com.zhi.app.model.WordButton;
import com.zhi.app.myview.MyGridView;
import com.zhi.app.util.Utils;

import android.R.integer;
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
import android.widget.Toast;

public class MainActivity extends Activity implements IWordButtonClickListener {

	private ImageView mViewPan; // 唱片
	private ImageView mViewPanBar; // 唱片旁边的那根棍子^-^
	private ImageButton mStartPlay; // 播放按钮
	private MyGridView mGridView; // 最下方的文字按钮布局
	private LinearLayout ll_select_word_container; // 待填充文字按钮容器

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
		
		//设置文字按钮点击事件
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
	 * <p>根据歌曲名称长度设置显示界面数据</p>
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
			holder.viewButton.setTextColor(Color.RED);
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
		String[] currStageSongData = SongData.SONG_INFO[stageIndex];
		song.setSongName(currStageSongData[SongData.INDEX_SONG_NAME]);
		song.setSongFileName(currStageSongData[SongData.INDEX_FILE_NAME]);
		
		return song;
	}

	/**
	 * 加载已选文字界面<br/>
	 * 		<p>根据当前歌曲的名称的个数来加载显示的方框数</p>
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
	}
	
	/**
	 * 点击待选文字按钮，将该文字填充至已选框区域
	 * @param wordButton
	 */
	private void setWordToSelectedPanel(WordButton wordButton) {
		// 遍历已选文字列表，如果为空，说明该位置还没有填充文字，则可以将待选文字框中的文字放置在该位置上
		for (int i = 0; i < this.mHadSelectDataList.size(); i++) {
			WordButton word = this.mHadSelectDataList.get(i);
			if(word.wordText.trim() == "") {
				
				// 设置已选文字框中的文字可见
				word.viewButton.setText(wordButton.wordText);
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
}



















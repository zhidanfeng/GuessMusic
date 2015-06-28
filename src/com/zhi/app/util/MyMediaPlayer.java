package com.zhi.app.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐播放类
 * 
 * @author zhidf
 * @date 2015.6.28
 */
public class MyMediaPlayer {

	private static MediaPlayer mPlayer;

	/** 确定按钮音效 */
	public static final int INDEX_CONFIRM_SOUNDEFFECT = 0;
	/** 取消按钮音效 */
	public static final int INDEX_CANCEL_SOUNDEFFECT = 1;
	/** 金币掉落音效 */
	public static final int INDEX_COIN_SOUNDEFFECT = 2;

	private static String[] mSoundEffect = new String[] { "enter.mp3", "cancel.mp3", "coin.mp3" };
	private static MediaPlayer[] mSoundEffectPlayers = new MediaPlayer[mSoundEffect.length];

	/**
	 * 播放音乐
	 * 
	 * @param context
	 *            上下文
	 * @param fileName
	 *            音乐文件名称，需放在assets目录下，例“江南.mp3”
	 */
	public static void play(Context context, String fileName) {
		try {

			if (mPlayer == null) {
				mPlayer = new MediaPlayer();
			}

			// 重置状态，这一步很有必要
			// 针对再次播放时，让其处于可以播放的状态
			mPlayer.reset();

			AssetManager assetManager = context.getAssets();
			AssetFileDescriptor fd = assetManager.openFd(fileName);
			mPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());

			mPlayer.prepare();

			mPlayer.start();

		} catch (Exception e) {

		}
	}

	/**
	 * 停止播放音乐
	 */
	public static void stop() {
		if (mPlayer != null) {
			mPlayer.stop();
		}
	}

	/**
	 * 播放音效
	 * 
	 * @param context
	 * @param index
	 */
	public static void playSoundEffect(Context context, int index) {
		try {
			
			if(mSoundEffectPlayers[index] == null) {
				mSoundEffectPlayers[index] = new MediaPlayer();
			}
			
			mSoundEffectPlayers[index].reset();
			
			AssetManager assetManager = context.getAssets();
			AssetFileDescriptor fd = assetManager.openFd(mSoundEffect[index]);
			mSoundEffectPlayers[index].setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
			
			mSoundEffectPlayers[index].prepare();
			
			mSoundEffectPlayers[index].start();
			
		} catch (Exception e) {
			
		}
	}
}

package com.zhi.app.model;

/**
 * 歌曲类
 * @author zhidf
 *
 */
public class Song {
	private String songName; // 歌曲名称
	private String songFileName; // 歌曲文件名称
	private int songNameLength; // 歌曲名称长度

	/**
	 * 将歌曲名称转成一个一个的字符，例：江南->江 南
	 * @return
	 */
	public char[] convertSongNameToArray() {
		return songName.toCharArray();
	}
	
	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
		// 设置歌曲名称的长度，用于设置界面上的方框个数
		this.songNameLength = songName.length();
	}

	public String getSongFileName() {
		return songFileName;
	}

	public void setSongFileName(String songFileName) {
		this.songFileName = songFileName;
	}

	public int getSongNameLength() {
		return songNameLength;
	}
}

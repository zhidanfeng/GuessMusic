package com.zhi.app.model;

import android.widget.Button;

public class WordButton {
	
	public int wordIndex;
	public String wordText;
	public boolean isVisible;
	
	public Button viewButton;
	
	public WordButton() {
		this.isVisible = true;
		this.wordText = "";
	}
}

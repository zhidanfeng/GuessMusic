package com.zhi.app.business;

public class FloatBtnBusiness {
	private static FloatBtnBusiness instance = null;
	
	public FloatBtnBusiness getInstance() {
		if(instance == null) {
			instance = new FloatBtnBusiness();
		}
		
		return instance;
	}
	
	public boolean handleCoin() {
		return true;
	}
}

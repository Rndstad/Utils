package net.amoebaman.utils.chat;

public enum CustomChar {
	
	LIGHT_BLOCK('\u2591'),
	MEDIUM_BLOCK('\u2592'),
	DARK_BLOCK('\u2593'),
	SOLID_BLOCK('\u2588'),
	;
	
	public char c;
	
	private CustomChar(char c) {
		this.c = c;
	}
	
	public String toString(){
		return "" + c;
	}
	
}

package com.android.shuomi.groupon;

public class ListItemUnion {	
	public static final int STRING = 0;
	public static final int IMAGE = 1;
	
	private int mType = -1;	
	private String mString = null;	
	private DyncImage mImage = null;
	
	public ListItemUnion( DyncImage image ) {
		mImage = image;
		mType = IMAGE;
	}
	
	public ListItemUnion( int resId, String url ) {
		setDyncImage( resId, url );
	}
	
	public void setDyncImage( int resId, String url ) {
		mImage = new DyncImage( resId, url );
		mType = IMAGE;
	}
	
	public DyncImage getDyncImage() {
		return mImage;
	}
	
	public ListItemUnion( String string ) {
		setString( string );
	}
	
	public String getString() {
		return this.mString;
	}
	
	public int getType() {
		return mType;
	}
	
	public void setString( String string ) {
		this.mString = string;
		mType = STRING;
	}
}

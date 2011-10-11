package com.android.shuomi.groupon;

import android.graphics.drawable.Drawable;

public class DyncImage {
	private int mResId = 0;
	private String mUrl = null;
	private Drawable mDrawable = null;
	
	public DyncImage( int resId, String url ) {
		setDyncImage( resId, url );
	}
	
	public void setDyncImage( int resId, String url ) {
		mResId = resId;
		mUrl = url;
	}
	
	public int getResourceId() {
		return mResId;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public Drawable getDrawable() {
		return mDrawable;
	}
	
	public void setDrawable( Drawable drawable ) {
		mDrawable = drawable;
	}
}

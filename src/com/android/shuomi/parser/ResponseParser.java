package com.android.shuomi.parser;

import java.util.ArrayList;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.util.Util;

public class ResponseParser {
	
	static final public int TYPE_ARRAY = 0;
	static final public int TYPE_ARRAY_LIST = 1;
	
	protected int mType = -1;
	protected String[] mItems = null;
	protected ArrayList<String[]> mItemsList = null;
	protected int mPage = 1;
	
	public ResponseParser( String inputStream, String requestAction ) {
		parse( inputStream, getKeyByAction( requestAction ) );
	}
	
	protected ResponseParser() {
		
	}
	
	private String getKeyByAction( String action ) {
		String key = null;
		
		if ( action.equals( REQUEST.LIST_PROVINCE ) ) {
			key = RESPONSE.PARAM_PROVINCE;
		}
		else if ( action.equals( REQUEST.LIST_CITY ) ) {
			key = RESPONSE.PARAM_CITY;
		}
		
		return key;
	}
	
	protected void parse( String inputStream, String key ) {
		if ( Util.isValid( key ) ) {
			SimpleJsonArrayParser parser = new SimpleJsonArrayParser( inputStream, key );
			mItems = parser.getItems();
			mType = TYPE_ARRAY;
		}
	}
	
	public String[] getData() {
		return mItems;
	}
	
	public ArrayList<String[]> getDataList() {
		return mItemsList;
	}
	
	public int getDataType() {
		return mType;
	}
	
	public int getPageCount() {
		return mPage;
	}
}

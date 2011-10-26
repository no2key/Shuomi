package com.android.shuomi.parser;

public class ShopListParser extends JsonArrayParser {

	static public String PARAM_NAME = "name";
	static public String PARAM_ADDR = "addr";
	static public String PARAM_TEL = "tel";
	static public String PARAM_X = "x";
	static public String PARAM_Y = "y";	
	
	static private String[] mParams = { PARAM_NAME, PARAM_ADDR, PARAM_TEL, PARAM_X, PARAM_Y };
	
	public ShopListParser( String inputStream ) {
		super( inputStream, mParams );
	}
}

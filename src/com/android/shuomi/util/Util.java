package com.android.shuomi.util;

public class Util {
	
	public static boolean isValid( String string ) {
		return ( string != null && string.length() > 0 );
	}
	
	public static boolean isValid( String[] array ) {
		return ( array != null && array.length > 0 );
	}
	
	public static int min( int first, int second ) {
		return ( first > second ) ? second : first;
	}
}

package com.android.shuomi.util;

import java.text.DecimalFormat;

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
	
	public static String getDiscount( String dividend, String divisor ) {
		String result = null;
		
		if ( Float.parseFloat( divisor ) != 0 ) {
			float discount = 10 * Float.parseFloat( dividend ) / Float.parseFloat( divisor );
			DecimalFormat df = new DecimalFormat( "########.0");
			discount = Float.parseFloat( df.format( discount ) );
			
			result = Float.toString( discount );
		}
		
		return result;
	}
}

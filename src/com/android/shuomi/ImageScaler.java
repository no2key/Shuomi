package com.android.shuomi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageScaler {

	public static Bitmap scaleBitmap( Bitmap bitmap, int w, int h ) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		Matrix matrix = new Matrix();
		float scaleWidht = ((float)w / width);
    	float scaleHeight = ((float)h / height);
    	matrix.postScale(scaleWidht, scaleHeight);
    	Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return newbmp;
	}
	
	public static Bitmap drawableToBitmap( Drawable drawable ) {
    	int width = drawable.getIntrinsicWidth();
    	int height = drawable.getIntrinsicHeight();
    	Bitmap bitmap = Bitmap.createBitmap(width, height,
    			drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
    	Canvas canvas = new Canvas(bitmap);
    	drawable.setBounds(0,0,width,height);
    	drawable.draw(canvas);
    	return bitmap;
    }
	
	public static Drawable scaleDrawable( Drawable drawable, int w, int h ) {
		Drawable dest = drawable;
		
		if ( drawable.getBounds().width() >= w ) {
			Bitmap bitmap = drawableToBitmap( drawable );
			bitmap = scaleBitmap( bitmap, w, h );
			dest = new BitmapDrawable( bitmap );  
		}
		
		return dest;
	}
	
	public static Drawable scaleDrawable( Bitmap bitmap, int w, int h ) {
		Drawable dest = null;
		
		if ( bitmap.getWidth() >= w ) {
			bitmap = scaleBitmap( bitmap, w, h );
			dest = new BitmapDrawable( bitmap );  
		}
		
		return dest;
	}
}

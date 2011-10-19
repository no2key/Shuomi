package com.android.shuomi.groupon;

import com.android.shuomi.ImageScaler;
import com.android.shuomi.NetworkRequestLayout;
import com.android.shuomi.R;
import com.android.shuomi.groupon.AsyncImageLoader.ImageCallback;
import com.android.shuomi.intent.GrouponDetailsRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.parser.ResponseParser;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.TextView;

public class GrouponDetailsView extends NetworkRequestLayout {
	
	static private final String TAG = "GrouponDetailsView"; 
	private String mItemId;
	private AsyncImageLoader mImageLoader = new AsyncImageLoader();
	
	public GrouponDetailsView( Context context, String itemId ) {
		super(context);		
		inflateLayout();
		
		mItemId = itemId;
		requestGrouponDetails();
	}
	
	private void requestGrouponDetails() {
		GrouponDetailsRequestIntent request = new GrouponDetailsRequestIntent( REQUEST.OBTAIN_DETAILS, mItemId );
		request.setSourceClass( getClass().getName() );
		sendRequest( request );
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.groupon_details_view, this, true );
	}

	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) {
		if ( requestAction.equals( REQUEST.OBTAIN_DETAILS ) ) {
			if ( dataType == ResponseParser.TYPE_ARRAY && data != null ) {
				updateView( ( String[] ) ( data ) );
				switchView();
			}
		}
	}
	
	private void switchView() {
		findViewById( R.id.empty_view_loading ).setVisibility( View.GONE );
		findViewById( R.id.content_view ).setVisibility( View.VISIBLE );
	}
	
	private void updateView( String[] items ) {
		String[] mKeys = { RESPONSE.PARAM_IMG_2, RESPONSE.PARAM_PROVIDER, 
				RESPONSE.PARAM_PRICE, RESPONSE.PARAM_ACTUAL_PRICE, RESPONSE.PARAM_TITLE, 
				RESPONSE.PARAM_EXPIRY, RESPONSE.FOLLOWED, RESPONSE.PARAM_ID };
		
		if ( Util.isValid( items ) ) {
			TextView price = (TextView) findViewById( R.id.price );
			price.setText( getContext().getString( R.string.original_price ) + items[2] + getContext().getString( R.string.chinese_yuan ) );
			
			TextView actualPrice = (TextView) findViewById( R.id.actual_price );
			actualPrice.setText( getContext().getString( R.string.actual_price ) + items[3] + getContext().getString( R.string.chinese_yuan ));
			
			String discountString = Util.getDiscount( items[3], items[2] );
			if ( Util.isValid( discountString ) ) {
				TextView discount = (TextView) findViewById( R.id.discount );
				discount.setText( discountString + getContext().getString( R.string.discount_symbol ) );
			}
			
			TextView details = (TextView) findViewById( R.id.description );
			details.setText( items[4] );
			
			TextView provider = (TextView) findViewById( R.id.provider );
			provider.setText( getContext().getString( R.string.provider ) + items[1] );
			
			TextPaint paint1 = provider.getPaint();
			float len1 = paint1.measureText( ( String )provider.getText() );
			
			Log.d( TAG, "provider width = " + provider.getWidth() + ", " + len1 );
			
			TextView follower = (TextView) findViewById( R.id.followed );
			follower.setText( items[6] + getContext().getString( R.string.people ) + getContext().getString( R.string.favorites ) );
			
			//measureView( follower );
			Log.d( TAG, "follower width = " + follower.getWidth() );
			
			int[] time = getExpireTime( Long.parseLong(items[5]) - System.currentTimeMillis() );
			String expiryBy = getContext().getString( R.string.expiry_by );
			
			if ( time[0] > 0 ) {
				expiryBy += time[0] + getContext().getString( R.string.day );
				expiryBy += time[1] + getContext().getString( R.string.hour );
			}
			else if ( time[1] > 0 ) {
				expiryBy += time[1] + getContext().getString( R.string.hour );
			}
			else {
				expiryBy += getContext().getString( R.string.less_than_one ) + getContext().getString( R.string.hour );
			}
			
			TextView expiry = (TextView) findViewById( R.id.expiry );
			expiry.setText( expiryBy );
			
			TextPaint paint = expiry.getPaint();
			float len = paint.measureText( ( String )expiry.getText() );

			//measureView( expiry );
			Log.d( TAG, "expiry width = " + expiry.getWidth() + ", " + len + ", " + expiry.getPaddingLeft()
					+ ", " + expiry.getPaddingRight() );
			
			Log.d( TAG, "view width = " + getWidth() );
			
			final ImageView image = (ImageView) findViewById( R.id.demo_big_image );
			Log.d( TAG, "dim = " + image.getWidth() + "," + image.getHeight() );
			
			Drawable drawable = mImageLoader.loadDrawable( items[0], new ImageCallback() {

				public void imageLoaded( Drawable imageDrawable, String imageUrl ) {
					Bitmap bitmap = ImageScaler.drawableToBitmap( imageDrawable );
					int height = bitmap.getHeight();
					int width = bitmap.getWidth();
					Log.d( TAG,  "bitmap " + width + ", " + height );
					int expectedWidth = image.getWidth();
					int expectedHeight = height * expectedWidth / width;
					Log.d( TAG,  "expected " + expectedWidth + ", " + expectedHeight );
					Drawable newImageDrawable = ImageScaler.scaleDrawable( bitmap, expectedWidth, expectedHeight );
					image.setImageDrawable( newImageDrawable );
				}
			});
		}
	}
	
	private int[] getExpireTime( long delta ) {
		int[] time = { 0, 0 };
		
		if ( delta > 0 ) {
			long hours = (delta / 1000 / 3600);
			time[0] = (int) (hours / 24);
			time[1] = (int) (hours % 24); 
		}
		
		return time;
	}
	
//	private void measureView( View child ) {
//        ViewGroup.LayoutParams p = child.getLayoutParams();
//        
//        if ( p == null ) {
//            p = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//        }
//
//        int childWidthSpec = ViewGroup.getChildMeasureSpec( 0, 0 + 0, p.width );
//        int lpHeight = p.height;
//        int childHeightSpec;
//        
//        if ( lpHeight > 0 ) {
//            childHeightSpec = MeasureSpec.makeMeasureSpec( lpHeight, MeasureSpec.EXACTLY );
//        } 
//        else {
//            childHeightSpec = MeasureSpec.makeMeasureSpec( 0, MeasureSpec.UNSPECIFIED );
//        }
//        child.measure( childWidthSpec, childHeightSpec );
//    }
}

package com.android.shuomi.groupon;

import java.util.ArrayList;

import com.android.shuomi.ImageScaler;
import com.android.shuomi.NetworkRequestLayout;
import com.android.shuomi.R;
import com.android.shuomi.groupon.AsyncImageLoader.ImageCallback;
import com.android.shuomi.intent.GrouponDetailsRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.parser.ResponseParser;
import com.android.shuomi.parser.ShopListParser;
import com.android.shuomi.util.EventIndicator;
import com.android.shuomi.util.Util;
import com.android.shuomi.ServiceListView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GrouponDetailsView extends NetworkRequestLayout {
	
	static private final String TAG = "GrouponDetailsView"; 
	private String mItemId;
	private AsyncImageLoader mImageLoader = new AsyncImageLoader();
	private String mUrl = null;
	private String mShopList = null;
	
	public GrouponDetailsView( Context context, String itemId ) {
		super(context);		
		inflateLayout();
		registerButtonActions();
		mItemId = itemId;
		requestGrouponDetails();
	}
	
	private void registerButtonActions() {
		Button favorite = (Button) findViewById( R.id.btn_favorites );
		favorite.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onAddToFavorite();
			}
		});
		
		Button share = (Button) findViewById( R.id.btn_share );
		share.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onShare();
			}
		});
		
		Button address = (Button) findViewById( R.id.btn_address );
		address.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onViewAddress();
			}
		});
		
		Button purchase = (Button) findViewById( R.id.btn_purchase );
		purchase.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPurchase();
			}
		});
	}
	
	private void onAddToFavorite() {
		
	}
	
	private void onShare() {
		showSharedByDialog();
		
//		WindowManager wm = (WindowManager)getContext().getSystemService( "window" );  
//		WindowManager.LayoutParams params = new WindowManager.LayoutParams();  
//		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;  
//		  
//		params.width = WindowManager.LayoutParams.WRAP_CONTENT;  
//		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		//params.
//		  
//		LayoutInflater inflater = LayoutInflater.from( getContext() );
//		View shareView = inflater.inflate( R.layout.groupon_share_view, null );
//		wm.addView( shareView, params );
	}
	
	private void showSharedByDialog() {
		LayoutInflater inflater = LayoutInflater.from( getContext() );
		View shareView = inflater.inflate( R.layout.groupon_share_view, null );
		
		AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
		builder.setTitle( R.string.please_select_shared_by );
		builder.setView( shareView );    	
    	AlertDialog alert = builder.create();
    	addSharedViewListener( alert, shareView );
    	alert.show();
	}
	
	private void addSharedViewListener( final Dialog parent, View view ) {
		view.findViewById( R.id.btn_microblog ).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				parent.dismiss();				
			}
		});
		
		view.findViewById( R.id.btn_email ).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				parent.dismiss();
				sharedByEmail();
			}
		});
		
		view.findViewById( R.id.btn_cancel ).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				parent.dismiss();
			}
		});
	}
	
	private void sharedByMicroBlog() {
		
	}
	
	private void sharedByEmail() {
		( ( ServiceListView ) getContext() ).goToNextView( new GrouponSharedByEmailView( getContext(), mItemId ) );
	}
	
	private void onViewAddress() {
		if ( Util.isValid( mShopList ) ) {
			ShopListParser parser = new ShopListParser( mShopList );
			ArrayList<String[]> list = parser.getDataList();
			
			if ( list != null && list.size() > 0 ) {
				( ( ServiceListView ) getContext() ).goToNextView( new ShopListView( getContext(), list ) );
			}
			else {
				EventIndicator.showToast( getContext(), getContext().getString( R.string.no_shop_address ) );
			}
		}
		else {
			EventIndicator.showToast( getContext(), getContext().getString( R.string.no_shop_address ) );
		}
	}
	
	private void onPurchase() {
		getContext().startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( mUrl ) ) );     
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
		if ( Util.isValid( items ) ) {
			setBigImage( items[0] );
			setPrices( items[2], items[3] );
			
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
			
			TextView expiry = (TextView) findViewById( R.id.expiry );
			expiry.setText( getExpiryLabel( Long.parseLong(items[5]) - System.currentTimeMillis() ) );
			
			TextPaint paint = expiry.getPaint();
			float len = paint.measureText( ( String )expiry.getText() );

			//measureView( expiry );
			Log.d( TAG, "expiry width = " + expiry.getWidth() + ", " + len + ", " + expiry.getPaddingLeft()
					+ ", " + expiry.getPaddingRight() );
			
			Log.d( TAG, "view width = " + getWidth() );
			
			mUrl = items[8];
			mShopList = items[9];
			Log.d( TAG, "shoplist = " + items[9] + ", len = " + items[9].length() );
		}
	}
	
	private void setPrices( String originalPrice, String actualPrice ) {
		TextView priceView = (TextView) findViewById( R.id.price );
		priceView.setText( getContext().getString( R.string.original_price ) + originalPrice + getContext().getString( R.string.chinese_yuan ) );
		
		TextView actualPriceView = (TextView) findViewById( R.id.actual_price );
		actualPriceView.setText( getContext().getString( R.string.actual_price ) + actualPrice + getContext().getString( R.string.chinese_yuan ));
		
		String discountString = Util.getDiscount( actualPrice, originalPrice );
		if ( Util.isValid( discountString ) ) {
			TextView discount = (TextView) findViewById( R.id.discount );
			discount.setText( discountString + getContext().getString( R.string.discount_symbol ) );
		}
	}
	
	private void setBigImage( String uri ) {
		final ImageView image = (ImageView) findViewById( R.id.demo_big_image );
		Log.d( TAG, "dim = " + image.getWidth() + "," + image.getHeight() );
		
		@SuppressWarnings("unused")
		Drawable drawable = mImageLoader.loadDrawable( uri, new ImageCallback() {

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
				//switchView();
			}
		});
	}
	
	private String getExpiryLabel( long delta ) {
		String label;
		
		if ( delta > 0 ) {
			int[] time = getExpireTime( delta );
			label = getContext().getString( R.string.expiry_by );
			
			if ( time[0] > 0 ) {
				label += time[0] + getContext().getString( R.string.day );
				label += time[1] + getContext().getString( R.string.hour );
			}
			else if ( time[1] > 0 ) {
				label += time[1] + getContext().getString( R.string.hour );
			}
			else {
				label += getContext().getString( R.string.less_than_one ) + getContext().getString( R.string.hour );
			}
		}
		else {
			label = getContext().getString( R.string.groupon_expired );
		}
		
		return label;
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
}

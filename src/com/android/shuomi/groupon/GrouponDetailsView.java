package com.android.shuomi.groupon;

import java.sql.Date;

import com.android.shuomi.NetworkRequestLayout;
import com.android.shuomi.R;
import com.android.shuomi.intent.GrouponDetailsRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.parser.ResponseParser;
import com.android.shuomi.util.Util;

import android.R.integer;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class GrouponDetailsView extends NetworkRequestLayout {
	
	static private final String TAG = "GrouponDetailsView"; 
	private String mItemId;
	
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
			
			TextView follower = (TextView) findViewById( R.id.followed );
			follower.setText( items[6] + getContext().getString( R.string.people ) + getContext().getString( R.string.favorites ) );
			
			int[] time = getExpireTime( Long.parseLong(items[5]) - System.currentTimeMillis() );
			String expiryBy = getContext().getString( R.string.expiry_by );
			//if ()
			
			Date expiry = new Date( Long.parseLong(items[5]) );
			Log.d( TAG, expiry.toLocaleString() );
			
			long current = System.currentTimeMillis();
			Date currentDate = new Date( current );
			Log.d( TAG, currentDate.toLocaleString() );
			
			long delta = Long.parseLong(items[5]) - System.currentTimeMillis();
			Date deltaTime = new Date( delta );
			Log.d( TAG, deltaTime.toLocaleString() );
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
}

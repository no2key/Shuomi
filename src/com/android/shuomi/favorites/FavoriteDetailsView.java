package com.android.shuomi.favorites;

import java.util.ArrayList;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.groupon.GrouponSharedByEmailView;
import com.android.shuomi.groupon.ShopListView;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.parser.ShopListParser;
import com.android.shuomi.util.EventIndicator;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavoriteDetailsView extends LinearLayout {
	
	//static final private String TAG = "FavoriteDetailsView";
	
	private String[] mItems;
	
	public FavoriteDetailsView( Context context, String items[] ) {
		super( context );
		mItems = items;
		inflateLayout();
		setupWidgets();
		registerButtonActions();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.favorite_details_view, this, true );
	}
	
	final String[] columns = { /*RESPONSE.PARAM_IMG_2,*/ 
			RESPONSE.PARAM_PROVIDER, RESPONSE.PARAM_PRICE, RESPONSE.PARAM_ACTUAL_PRICE, 
			RESPONSE.PARAM_TITLE, RESPONSE.PARAM_EXPIRY, RESPONSE.FOLLOWED, 
			RESPONSE.PARAM_ID, RESPONSE.PARAM_URL, RESPONSE.PARAM_SHOPLIST, 
			RESPONSE.PARAM_TIMESTAMP, BaseColumns._ID };
	
	private void setupWidgets() {
		if ( Util.isValid( mItems ) ) {
			
			setPrices( mItems[1], mItems[2] );
			
			TextView details = (TextView) findViewById( R.id.description );
			details.setText( mItems[3] );
			
			TextView provider = (TextView) findViewById( R.id.provider );
			provider.setText( getContext().getString( R.string.provider ) + mItems[0] );
			
			TextView expiry = (TextView) findViewById( R.id.expiry );
			expiry.setText( getExpiryLabel( Long.parseLong(mItems[4]) - System.currentTimeMillis() ) );
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
	
	private void registerButtonActions() {
		
		Button share = (Button) findViewById( R.id.btn_forward );
		share.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sharedByEmail();
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
	
	private void sharedByEmail() {
		( ( ServiceListView ) getContext() ).goToNextView( new GrouponSharedByEmailView( getContext(), mItems[6] ) );
	}
	
	private void onViewAddress() {
		if ( Util.isValid( mItems[8] ) ) {
			ShopListParser parser = new ShopListParser( mItems[8] );
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
		getContext().startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( mItems[7] ) ) );     
	}
}

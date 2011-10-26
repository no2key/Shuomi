package com.android.shuomi.tools;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.UpdatableView;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.network.NetworkService;
import com.android.shuomi.network.NetworkSession;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ExpressQueryView extends LinearLayout implements UpdatableView {

	private static final String TAG = "ExpressQueryView"; 
	private Spinner mExpressList;
	
	public ExpressQueryView( Context context ) {
		super(context);
		
		inflateLayout();
		requestExpressList();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.express_query_view, this, true );
		
		setupTitle();
	}
	
	private void setupTitle() {
		TextView title = (TextView) findViewById( R.id.titlebar_label );
		title.setText( R.string.express_query );
		
		findViewById( R.id.titlebar_button ).setVisibility( View.GONE );
	}
	
	private void requestExpressList() {
		RequestIntent request = new RequestIntent( REQUEST.EXPRESS_LIST );
		request.setSourceClass( getClass().getName() );
		request.setResponseAction( RESPONSE.HTTP_RESPONSE_EXPRESS );
		NetworkSession.send( request );
	}

	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) {
		Log.d( TAG, "express updated" );
		
	}
}

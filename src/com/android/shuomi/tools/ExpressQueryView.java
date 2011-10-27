package com.android.shuomi.tools;

import java.util.ArrayList;

import com.android.shuomi.EnhancedEditText;
import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.UpdatableView;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.network.NetworkSession;
import com.android.shuomi.parser.ResponseParser;
import com.android.shuomi.util.EventIndicator;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ExpressQueryView extends LinearLayout implements UpdatableView {

	private static final String TAG = "ExpressQueryView"; 
	private Spinner mExpressList = null;
	private ArrayList<String[]> mDataList = null;
	private EnhancedEditText mEditOrder = null;
	
	public ExpressQueryView( Context context ) {
		super(context);
		
		inflateLayout();
		requestExpressList();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.express_query_view, this, true );
		
		setupTitle();
		addButtonListener();
		mEditOrder = (EnhancedEditText) findViewById( R.id.edit_query_no );
	}
	
	private void setupTitle() {
		TextView title = (TextView) findViewById( R.id.titlebar_label );
		title.setText( R.string.express_query );
		
		findViewById( R.id.titlebar_button ).setVisibility( View.GONE );
	}
	
	private void addButtonListener()
	{
		Button button = (Button) findViewById( R.id.btn_query );
		button.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				queryExpress();								
			}
		});
	}
	
	private void queryExpress()
	{
		String orderId = mEditOrder.getText().toString();
		
		if ( Util.isValid( orderId ) )
		{
			int position = mExpressList.getSelectedItemPosition();
			String expressId = mDataList.get( position )[0];
			( ( ServiceListView ) getContext() ).goToNextView( new ExpressQueryResultView( getContext(), orderId, expressId ) );
		}
		else
		{
			EventIndicator.showToast( getContext(), getContext().getString( R.string.please_input_express_no ) );
		}
	}
	
	private void setupExpressList( ArrayList<String> list ) {
		mExpressList = (Spinner) findViewById( R.id.spinner_companies );  
  
		if ( list != null && list.size() > 0 )
		{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>( getContext(), android.R.layout.simple_spinner_item, list );  
	        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );  
	          
	        mExpressList.setAdapter( adapter );
		}
	}
	
	private void requestExpressList() {
		RequestIntent request = new RequestIntent( REQUEST.EXPRESS_LIST );
		request.setSourceClass( getClass().getName() );
		request.setResponseAction( RESPONSE.HTTP_RESPONSE_EXPRESS );
		NetworkSession.send( request );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) {
		Log.d( TAG, "express updated" );
		
		if ( requestAction.equals( REQUEST.EXPRESS_LIST ) )
		{
			if ( dataType == ResponseParser.TYPE_ARRAY_LIST )
			{
				mDataList = ( ArrayList<String[]> ) data;
				ArrayList<String> list = getCompanyArrayList( mDataList );
				setupExpressList( list );
			}
		}
	}

	private ArrayList<String> getCompanyArrayList( ArrayList<String[]> dataList ) 
	{
		ArrayList<String> list = null;
		
		if ( dataList != null && dataList.size() > 0 )
		{
			list = new ArrayList<String>();
			
			for ( String[] items : dataList )
			{
				list.add( items[1] );
			}
		}

		return list;
	}
}

package com.android.shuomi.tools;

import com.android.shuomi.R;
import com.android.shuomi.UpdatableView;
import com.android.shuomi.intent.ExpressQueryRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.network.NetworkSession;
import com.android.shuomi.parser.ResponseParser;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpressQueryResultView extends LinearLayout implements UpdatableView {

	public ExpressQueryResultView( Context context, String orderId, String expressId ) {
		super(context);
		inflateLayout();
		sendQueryRequest( orderId, expressId );
	}
	
	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.express_result, this, true );
		setupTitle();
	}
	
	private void setupTitle() {
		TextView title = (TextView) findViewById( R.id.titlebar_label );
		title.setText( R.string.express_query_result );
		
		findViewById( R.id.titlebar_button ).setVisibility( View.GONE );
	}
	
	private void sendQueryRequest( String orderId, String expressId )
	{
		if ( Util.isValid( orderId ) && Util.isValid( expressId ) )
		{
			ExpressQueryRequestIntent request = new ExpressQueryRequestIntent( getClass().getName(), expressId, orderId );
			NetworkSession.send( request );
		}
	}

	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) 
	{
		Log.d( "WebView", "update" );
		if ( requestAction.equals( REQUEST.EXPRESS_QUERY ) )
		{
			if ( dataType == ResponseParser.TYPE_ARRAY )
			{
				String[] items = (String[]) data;
				updateView( items );
			}
		}
	}

	private void updateView( String[] items ) {
		if ( Util.isValid( items ) )
		{
			WebView view = (WebView) findViewById( R.id.content_view );
			view.getSettings().setJavaScriptEnabled( false );
	        view.setScrollBarStyle( 0 );
	        String summary = "<html><body>" + items[0] + "</body></html>";
	        view.loadDataWithBaseURL( null, summary, "text/html", "utf-8", null );
	        
	        switchView();
		}
	}

	private void switchView() {
		findViewById( R.id.empty_view_loading ).setVisibility( View.GONE );
		findViewById( R.id.content_view ).setVisibility( View.VISIBLE );
	}
}

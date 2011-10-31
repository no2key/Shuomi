package com.android.shuomi.groupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.shuomi.NetworkRequestLayout;
import com.android.shuomi.R;

abstract public class ListLayout extends NetworkRequestLayout {
	abstract protected String getRequestAction();
	abstract protected int getLayoutResId();
	
	private TextView mTitle = null;
	private Button mButton = null;
	private ListView mList = null;
	
	public ListLayout( Context context ) {
		super( context );
		inflateLayout();
		getChildWidgets();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( getLayoutResId(), this, true );
	}
	
	private void getChildWidgets() {
		mTitle = ( TextView ) findViewById( R.id.titlebar_label );		
		mButton = ( Button ) findViewById( R.id.titlebar_button );

		mList = ( ListView ) findViewById( R.id.list );
		mList.setEmptyView( findViewById( R.id.text_no_item ) );
		
		mList.setOnItemClickListener( new OnItemClickListener() {                            
			  
		    public void onItemClick( AdapterView<?> arg0, View arg1, int arg2,  long arg3 ) { 
		    	onItemSingleClick( arg1, arg2, arg3 );
		    }
		} );
	}
	
	protected void onItemSingleClick( View view, int position, long rowId ) {
		// leave for derived class to implement
	}
	
	protected void setTitleText( int id ) {
		mTitle.setText( id );
	}
	
	protected void setTitleText( String label ) {
		mTitle.setText( label );
	}
	
	protected void setButtonText( int id ) {
		mButton.setText( id );
	}
	
	protected void enableButton( boolean enable ) {
		mButton.setVisibility( enable ? View.VISIBLE : View.GONE );
	}
	
	protected ListView getList() {
		return mList;
	}
}

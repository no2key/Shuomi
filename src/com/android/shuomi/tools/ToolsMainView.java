package com.android.shuomi.tools;

import java.util.ArrayList;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ToolsMainView extends LinearLayout {

	private final int[] mLabelResIds = { R.string.weather_service, R.string.express_query, R.string.about_us };
	private final int WEATHER_SERVICE = 0;
	private final int EXPRESS_QUERY = 1;
	private final int ABOUT_US = 2;
	
	public ToolsMainView( Context context ) {
		super( context );

		inflateLayout();
		setupList();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.simple_list_with_titlebar, this, true );
		TextView title = (TextView) findViewById( R.id.titlebar_label );
		title.setText( R.string.utility_list );
		findViewById( R.id.titlebar_button ).setVisibility( View.GONE );
	}
	
	private ArrayList<String> getLabelList() {
		ArrayList<String> list = new ArrayList<String>();
		
		for ( int id : mLabelResIds ) {
			list.add( getContext().getString( id ) );
		}
		
		return list;
	}
	
	private void setupList() {
		ListView list = (ListView) findViewById( R.id.list );
		list.getBackground().setAlpha( UI.VIEW_DEFAULT_ALPHA );
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( getContext(), 
				R.layout.single_field_item, R.id.text_groupon_item, getLabelList() );
		list.setAdapter( adapter );
		setOnItemClickListener( list );
	}
	
	private void setOnItemClickListener( ListView list ) {
		list.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
				handleItemClick( position );			
			}
		});
	}
	
	private void handleItemClick( int position ) {
		switch ( position ) 
		{
		case WEATHER_SERVICE:
			( ( ServiceListView ) getContext() ).goToNextView( new WeatherServiceView( getContext() ) );
			break;
		case EXPRESS_QUERY:
			( ( ServiceListView ) getContext() ).goToNextView( new ExpressQueryView( getContext() ) );
			break;
		case ABOUT_US:
			showAboutUsDialog();
			break;
		}
	}
	
	private void showAboutUsDialog() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View view = layoutInflater.inflate( R.layout.about_view, null, true );
		
		AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
		builder.setIcon( android.R.drawable.ic_dialog_info );
		builder.setTitle( R.string.about_us );
		//builder.setMessage( R.string.about_us );
		builder.setView( view );

    	builder.setPositiveButton( R.string.dlg_btn_ok, new DialogInterface.OnClickListener() {				
			public void onClick( DialogInterface dialog, int which ) {
				dialog.dismiss();				
			}
		});
    	
    	AlertDialog alert = builder.create();
    	alert.show();
	}
}

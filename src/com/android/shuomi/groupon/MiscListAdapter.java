package com.android.shuomi.groupon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.shuomi.R;
import com.android.shuomi.groupon.AsyncImageLoader.ImageCallback;
import com.android.shuomi.util.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MiscListAdapter extends ArrayAdapter<ListItemUnion[]> 
{
	private AsyncImageLoader mImageLoader = new AsyncImageLoader();
	private Map<Integer, View> mViewMap = new HashMap<Integer, View>();
	private int[] mTitleResIds;
	private String[] mTitleLabelStrings;
	private int[] mItemFieldsResId;
	private int mResource;
	
	private Animation mRotateAnimation= AnimationUtils.loadAnimation( this.getContext(), R.anim.no_image_rotate );
	
	public MiscListAdapter( Context context, int resource, List<ListItemUnion[]> itemList,
			int[] titleResIds, String[] titleLabelStrings, int[] itemFieldsResId ) {
		super( context, 0, itemList );
		
		mResource = resource;
		mTitleResIds = titleResIds;
		mTitleLabelStrings = titleLabelStrings;
		mItemFieldsResId = itemFieldsResId;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View rowView = mViewMap.get( position );

		if ( rowView == null ) {
			LayoutInflater inflater = ((Activity) this.getContext()).getLayoutInflater();
			rowView = inflater.inflate( mResource, null );
						
			setFieldTitles( rowView, mTitleResIds, mTitleLabelStrings );
			setItems( rowView, mItemFieldsResId, getItem( position ) );
			
			mViewMap.put(position, rowView);
		}
		
		return rowView;
	}
	
	private void setFieldTitles( View rowView, int[] titleResIds, String[] titleLabelStrings ) {
		if ( rowView != null && titleResIds != null && titleLabelStrings != null ) {
			int length = Util.min( titleResIds.length, titleLabelStrings.length );
			
			for ( int i = 0; i < length; i ++ ) {
				setTextViewLabel( rowView, titleResIds[i], titleLabelStrings[i] );
			}
		}
	}
	
	private void setTextViewLabel( View parent, int textViewResId, String label ) {
		if ( parent != null && label != null && textViewResId != 0 ) {
			try {
				final TextView textView = ( TextView ) parent.findViewById( textViewResId );
				
				if ( textView != null ) {
					textView.setText( label );
				}
			}
			catch( ClassCastException e ) {
				Log.e( "MiscListAdapter", e.getMessage() );
			}
			
		}
	}
	
	private void setImageViewData( View parent, int imageViewResId, DyncImage image ) 
	{
		if ( parent != null && image != null && imageViewResId != 0 ) 
		{
			try 
			{
				final ImageView imageView = ( ImageView ) parent.findViewById( imageViewResId );
				
				if ( imageView != null ) 
				{
					Drawable drawable = mImageLoader.loadDrawable( image.getUrl(), new ImageCallback() 
					{
						public void imageLoaded( Drawable imageDrawable, String imageUrl ) 
						{
							imageView.clearAnimation();
							imageView.setImageDrawable( imageDrawable );
							imageView.setScaleType(  ScaleType.FIT_CENTER );
						}
					});
					
					if ( drawable == null ) 
					{
						imageView.setImageResource( image.getResourceId() );
						imageView.startAnimation( mRotateAnimation );
					}
				}
			}
			catch( ClassCastException e ) 
			{
				Log.e( "MiscListAdapter", e.getMessage() );
			}
		}
	}
	
	private void setItems( View rowView, int[] itemFieldsResId, ListItemUnion[] items ) 
	{
		if ( rowView != null && itemFieldsResId != null && items != null ) 
		{
			int length = Util.min( itemFieldsResId.length, items.length );
			
			for ( int i = 0; i < length; i ++ ) 
			{
				switch ( items[i].getType() ) 
				{
				case ListItemUnion.IMAGE:
					setImageViewData( rowView, itemFieldsResId[i], items[i].getDyncImage()  );
					break;
				case ListItemUnion.STRING:
					setTextViewLabel( rowView, itemFieldsResId[i], items[i].getString() );
					break;
				default:
					break;
				}
				
			}
		}
	}

}

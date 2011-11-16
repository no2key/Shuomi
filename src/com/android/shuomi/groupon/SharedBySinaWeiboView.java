package com.android.shuomi.groupon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.shuomi.R;
import com.weibo.net.RequestToken;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SharedBySinaWeiboView extends LinearLayout 
{
	private static final String URL_ACTIVITY_CALLBACK = "weiboandroidsdk://AuthorizationDone";
	private static final String FROM = "Shuomi";	
	private static final String CONSUMER_KEY = "316097992";
	private static final String CONSUMER_SECRET = "72800980b6d168fb6416b5debfc681ef";
	
	public SharedBySinaWeiboView( Context context, String text, String imageFile ) 
	{
		super( context );
		inflateLayout();
	}
	
	private void inflateLayout() 
	{
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.share_by_sina_weibo, this, true );
		setupTitle();
		goToAuthPage();
	}
	
	private void setupTitle() 
	{
		TextView title = (TextView) findViewById( R.id.titlebar_label );
		title.setText( R.string.share_dialog_title );
		
		findViewById( R.id.titlebar_button ).setVisibility( View.GONE );
	}

	private void goToAuthPage()
	{
		WebView view = (WebView) findViewById( R.id.content_view );
		view.getSettings().setJavaScriptEnabled( false );
        view.setScrollBarStyle( 0 );
        view.loadUrl( composeAuthUrl().toString() );
	}
	
	private Uri composeAuthUrl()
	{
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig( CONSUMER_KEY, CONSUMER_SECRET );
		
		Uri uri = null;
		
		try 
		{
			RequestToken requestToken = weibo.getRequestToken( getContext(), Weibo.APP_KEY, 
					Weibo.APP_SECRET, URL_ACTIVITY_CALLBACK );
			uri = Uri.parse(Weibo.URL_AUTHENTICATION + "?display=wap2.0&oauth_token=" + 
					requestToken.getToken() + "&from=" + FROM);
		}
		catch ( WeiboException e)
		{
			e.printStackTrace();
		}
		
		return uri;
	}
	
	private boolean saveDrawableToFile( Drawable image, String fileName )
	{
		boolean result = false;
		
		BitmapDrawable bitmapDrawable = ( BitmapDrawable ) image;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        
        File imageFile = getContext().getFileStreamPath( fileName );
        FileOutputStream fileStream;
		try 
		{
			fileStream = new FileOutputStream( imageFile, false );
			bitmap.compress( CompressFormat.PNG, 100, fileStream );
			fileStream.flush();
			fileStream.close();
			result = true;
		}		
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}

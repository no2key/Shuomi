package com.android.shuomi.groupon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.shuomi.R;
import com.android.shuomi.thirdparty.SinaWeiboHandlerView;
import com.android.shuomi.util.Util;
import com.weibo.net.RequestToken;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SharedBySinaWeiboView extends LinearLayout implements SinaWeiboHandlerView
{
	private static final String TAG = "SharedBySinaWeiboView";
	
	private static final String SINA_URL_SCHEME = "weiboandroidsdk";
	private static final String URL_ACTIVITY_CALLBACK = "weiboandroidsdk://AuthorizationDone";
	private static final String FROM = "Shuomi";	
	private static final String CONSUMER_KEY = "316097992";
	private static final String CONSUMER_SECRET = "72800980b6d168fb6416b5debfc681ef";
	
	public SharedBySinaWeiboView( Context context, String text, String imageFile ) 
	{
		super( context );
		inflateLayout();
		goToAuthPage();
	}
	
	private void inflateLayout() 
	{
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.share_by_sina_weibo, this, true );
		setupTitle();
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
		view.getSettings().setJavaScriptEnabled( true );
		view.getSettings().setSaveFormData( true );
        view.setScrollBarStyle( 0 );
        
        String uriStr = composeAuthUrl().toString();
        Log.d( TAG, "goto uri: " + uriStr );
        view.loadUrl( composeAuthUrl().toString() );
        Log.d( TAG, "goto uri done!" );
        
        view.setWebViewClient( new WebViewClient() 
        {
        	public boolean shouldOverrideUrlLoading(WebView view, String url) 
        	{
        		Uri uri = Uri.parse( url );
        		
        		if ( uri != null && uri.getScheme().equals( SINA_URL_SCHEME ) )
        		{
        			Log.d( TAG, url );
        			setupShareVars( uri, null, null );
        		}
        		else 
        		{
        			view.loadUrl( url );
				}
                
                return true;
        	}
        } );
        
        view.requestFocus();
	}
	
	private void setupShareVars( Uri uri, String content, String picPath )
	{
		if ( uri != null )
		{
			String oauth_verifier = uri.getQueryParameter( "oauth_verifier" );
			
			if ( Util.isValid( oauth_verifier ) )
			{
				Weibo weibo = Weibo.getInstance();
				weibo.addOauthverifier(oauth_verifier);
				
				try 
				{
					weibo.generateAccessToken( getContext(), null );
					
					weibo.share2weibo( getContext(), weibo.getAccessToken().getToken(), 
							weibo.getAccessToken().getSecret(), content, picPath );
				}
				catch (WeiboException e1) 
				{
					e1.printStackTrace();
				}
			}
		}		
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

	@Override
	public void processResponse(Uri uri) {
		Log.d( TAG, "received weibo auth done message" );
		
	}
}

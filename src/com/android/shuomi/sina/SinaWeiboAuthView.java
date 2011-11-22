package com.android.shuomi.sina;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.persistence.Preference;
import com.android.shuomi.thirdparty.SinaWeiboHandlerView;
import com.android.shuomi.util.EventIndicator;
import com.android.shuomi.util.Util;
import com.weibo.net.RequestToken;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

public class SinaWeiboAuthView extends LinearLayout implements SinaWeiboHandlerView {

	private static final String TAG = "SinaWeiboAuthView";
	
	private static final String SINA_URL_SCHEME = "weiboandroidsdk";
	private static final String URL_ACTIVITY_CALLBACK = "weiboandroidsdk://AuthorizationDone";
	private static final String FROM = "Shuomi";	
	public static final String CONSUMER_KEY = "316097992";
	private static final String CONSUMER_SECRET = "72800980b6d168fb6416b5debfc681ef";
	
	private String mText;
	private String mImageFile;
	private String mUri;
	
	public static String mVerifier;
	
	public SinaWeiboAuthView( Context context, String text, String uri, String imageFile ) 
	{
		super( context );
		
		mText = text;
		mUri = uri;
		mImageFile = imageFile;
		
		inflateLayout();
		getAuthUrl();
	}
	
	private void inflateLayout() 
	{
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.share_by_sina_weibo, this, true );
		//setupTitle();
	}
	
//	private void setupTitle() 
//	{
//		TextView title = (TextView) findViewById( R.id.titlebar_label );
//		title.setText( R.string.share_dialog_title );
//		
//		findViewById( R.id.titlebar_button ).setVisibility( View.GONE );
//	}
	
	private void getAuthUrl()
	{
		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage( Message msg ) 
			{
				if ( (String) msg.obj != null ) 
				{
					goToAuthPage( ( String ) msg.obj );
				}
			}
		};
		
		new Thread() 
		{
			public void run() 
			{
				Uri uri = composeAuthUrl();
				
				if ( uri != null && !TextUtils.isEmpty( uri.toString() ) )
				{
					handler.sendMessage( handler.obtainMessage( 0, uri.toString() ) );
				}
				else 
				{
					EventIndicator.showAlert( getContext(), R.string.network_error );
				}
			};
		}.start();
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

	private void goToAuthPage( String authUri )
	{
		final WebView view = ( WebView ) findViewById( R.id.content_view );
		view.getSettings().setJavaScriptEnabled( true );
		view.getSettings().setSupportZoom(true);
		view.getSettings().setBuiltInZoomControls( true );
		
        view.setScrollBarStyle( 0 );
        
        Log.d( TAG, "goto uri: " + authUri );
        view.loadUrl( authUri );
        Log.d( TAG, "goto uri done!" );
        
        view.setWebViewClient( new WebViewClient() 
        {
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) 
        	{
        		Uri uri = Uri.parse( url );
        		
        		if ( uri != null && uri.getScheme().equals( SINA_URL_SCHEME ) )
        		{
        			Log.d( TAG, url );
        			launchShare( uri, mText, mUri, mImageFile );
        		}
        		else 
        		{
        			view.loadUrl( url );
				}
                
                return true;
        	}
        	
        	@Override
        	public void onPageFinished (WebView view, String url)
        	{
        		Log.d( TAG, "onPageFinished" );
        		findViewById( R.id.empty_view_loading ).setVisibility( View.GONE );
        		view.setVisibility( View.VISIBLE );
        		view.requestFocus();
        	}
        	
        } );
	}
	
	static public void goToShareView( Context context, String accessToken, String tokenSecret, String content, String uri, String picPath, boolean finishSelf )
	{
		if ( TextUtils.isEmpty( accessToken ) )
		{
			EventIndicator.showToast( context, "token can not be null!" );
		}
		else if ( TextUtils.isEmpty( tokenSecret ) )
		{
			EventIndicator.showToast( context, "secret can not be null!" );
		}
		else if ( TextUtils.isEmpty(content) || TextUtils.isEmpty(picPath) )
		{
			EventIndicator.showToast( context, "weibo content can not be null!" );
		}
		else 
		{
			Bundle bundle = new Bundle();

			bundle.putString( SinaWeiboShareView.EXTRA_ACCESS_TOKEN, accessToken );
			bundle.putString( SinaWeiboShareView.EXTRA_TOKEN_SECRET, tokenSecret );
			bundle.putString( SinaWeiboShareView.EXTRA_WEIBO_CONTENT, content );
			bundle.putString( SinaWeiboShareView.EXTRA_PIC_URI, picPath );
			bundle.putString( SinaWeiboShareView.EXTRA_WEBPAGE_URI , uri );
			
			Weibo.getInstance().setupConsumerConfig( CONSUMER_KEY, CONSUMER_SECRET );
			( ( ServiceListView ) context ).goToNextView( new SinaWeiboShareView( context, bundle ), finishSelf );
		}
	}
	
	private void launchShare( Uri uri, String content, String webpageUri, String picPath )
	{
		if ( uri != null )
		{
			String oauth_verifier = uri.getQueryParameter( "oauth_verifier" );
			
			if ( Util.isValid( oauth_verifier ) )
			{
				mVerifier = oauth_verifier;
				
				Weibo weibo = Weibo.getInstance();
				weibo.addOauthverifier( oauth_verifier );
				
				try 
				{
					weibo.generateAccessToken( getContext(), null );
					saveAccessToken( weibo.getAccessToken().getToken(), weibo.getAccessToken().getSecret() );
					goToShareView( getContext(), weibo.getAccessToken().getToken(), 
							weibo.getAccessToken().getSecret(), content, webpageUri, picPath, true );
				}
				catch (WeiboException e1) 
				{
					e1.printStackTrace();
				}
			}
		}		
	}
	
	private void saveAccessToken( String token, String tokenSecret )
	{
		if ( !TextUtils.isEmpty( token ) && !TextUtils.isEmpty( tokenSecret ) )
		{
			SharedPreferences pref = getContext().getSharedPreferences ( "groupon", 0 );
			Editor editor = pref.edit();
			editor.putString( Preference.OAUTH_TOKEN,  token );
			editor.putString( Preference.OAUTH_TOKEN_SECRET,  tokenSecret );
			editor.commit();
		}
	}
	
	@Override
	public void processResponse(Uri uri) {
		Log.d( TAG, "received weibo auth done message" );
	}
}

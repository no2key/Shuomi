package com.android.shuomi.thirdparty;

import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SinaWeiboResponseHandler implements ExternalResponseHandler {

	private SinaWeiboHandlerView mView = null;
	private Context mContext = null;
	
	public SinaWeiboResponseHandler( Context context, SinaWeiboHandlerView view )
	{
		mContext = context;
		mView = view;
	}
	
	private void prepare( Intent response )
	{
		Uri uri = response.getData();
		String oauth_verifier = uri.getQueryParameter("oauth_verifier");
		
		Weibo weibo = Weibo.getInstance();
		weibo.addOauthverifier(oauth_verifier);
		
		try 
		{
			weibo.generateAccessToken( mContext, null );
		}
		catch (WeiboException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void processResponse( Intent response ) 
	{
		if ( mContext != null && mView != null )
		{
			prepare( response );
			mView.processResponse( null );
		}
	}
	
//	private void share()
//	{
//		Weibo weibo = Weibo.getInstance();
//		weibo.share2weibo( mContext, weibo.getAccessToken().getToken(), weibo.getAccessToken().getSecret(), 
//				content, picPath);
//	}

}

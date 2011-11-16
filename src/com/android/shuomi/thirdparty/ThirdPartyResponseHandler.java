package com.android.shuomi.thirdparty;

import android.content.Intent;

public class ThirdPartyResponseHandler 
{
	static public final String SCHEME_SINAWEIBO = "weiboandroidsdk";
	
	static public boolean processResponse( Intent response, ExternalResponseHandlerFactory factory )
	{
		boolean result = false;
		
		if ( response != null && factory != null )
		{
			result = process( response, factory );
		}
		
		return result;
	}

	private static boolean process( Intent response, ExternalResponseHandlerFactory factory ) 
	{
		boolean result = false;
		ExternalResponseHandler handler = null;
		
		if ( response.getAction().equals( "android.intent.action.VIEW" ) )
		{
			if ( response.getData().getScheme().equals( SCHEME_SINAWEIBO ) )
			{
				handler = factory.createHandler( ExternalResponseType.SINA_WEIBO );
			}
		}
		
		if ( handler != null )
		{
			handler.processResponse( response );
			result = true;
		}
		
		return result;
	}
}

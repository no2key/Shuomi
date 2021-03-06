package com.android.shuomi.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

import com.android.shuomi.util.Util;

import android.util.Log;

public class HttpService {
	private final int ERROR_GENERIC = -1;
    private final int ERROR_UNKNOWN_HOST = -2;
    private final int ERROR_PROTOCOL = -3;
	private HttpResponse mResponse = null;
	private String mBody = null;
	private String mCookie = null;
	private int mStatus = 0;
	private HashMap<String, String> mHeader = null;
	
	private void send( String url, String entity, boolean isHttpGet ) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter( CoreConnectionPNames.SO_TIMEOUT, 60000 );
		httpClient.getParams().setParameter( CoreConnectionPNames.CONNECTION_TIMEOUT, 60000 ); 
		
		try {
			if ( isHttpGet ) {
				HttpGet getRequest = new HttpGet( url );
				getRequest.setHeader("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1" );
				getRequest.setHeader( "Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8" );
				getRequest.setHeader( "Accept-Encoding", "gzip, deflate" );
				
				//Log.v( "HttpService", "send GET request" );
				Log.v( "HttpService", url );
				mResponse = httpClient.execute( getRequest );
			}
			else {
				HttpPost postRequest = new HttpPost( url );
				
				if ( entity != null && entity.length() > 0 ) {
					StringEntity se = new StringEntity( entity );  
					se.setContentEncoding( "UTF-8" );
					se.setContentType( "application/x-www-form-urlencoded" );
					
                    postRequest.setEntity( se );
					
					HttpEntity httpEntity = postRequest.getEntity();
					Log.d( "HttpService", getEntity( httpEntity ) );
				}
				
				postRequest.getParams().setBooleanParameter( CoreProtocolPNames.USE_EXPECT_CONTINUE, false );
				
				mResponse = httpClient.execute( postRequest );
			}
			
			if ( mResponse != null ) {
				mStatus = mResponse.getStatusLine().getStatusCode();
			    Log.v( "HttpService", "response received, status = " + String.valueOf(mStatus) );
			    mHeader = ( mResponse != null ) ? getHeaderMapInternal( mResponse ) : null;
			    mBody = ( mResponse != null ) ? getBodyInternal( mResponse ) : null;
			}
		}
		catch ( UnknownHostException e ) {
			Log.d( "HttpService", "unknown host!!" );
			Log.d( "HttpService", e.getMessage() );
			mStatus = ERROR_UNKNOWN_HOST;
            mBody = e.getMessage();
		}
		catch ( ClientProtocolException e ) {
			mStatus = ERROR_PROTOCOL;
			mBody = e.getMessage();
		}
		catch ( IOException e ) {
			mStatus = ERROR_GENERIC;
			mBody = e.getMessage();
		}
		
		httpClient.getConnectionManager().shutdown();
	}
	
	public void get( String url ) {
		send( url, null, true );
	}
	
//	protected String retrieveInputStream( HttpResponse response ) {
//		HttpEntity httpEntity = response.getEntity();
//        int length = (int) httpEntity.getContentLength();        
//        length =  (length < 0) ? 10240 : length;
//        
//        StringBuffer stringBuffer = new StringBuffer(length);
//        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(
//                    httpEntity.getContent(), HTTP.UTF_8 );
//            
//            char buffer[] = new char[length];
//            int count;
//            while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
//                stringBuffer.append(buffer, 0, count);
//            }
//        } 
//        catch  (UnsupportedEncodingException e ) {
//            Log.e( "HttpService", e.getMessage() );
//        } 
//        catch ( IllegalStateException e ) {
//            Log.e( "HttpService", e.getMessage() );
//        } 
//        catch ( IOException e ) {
//            Log.e( "HttpService", e.getMessage() );
//        }
//        
//        return stringBuffer.toString();
//    }
	
	private String getEntity( HttpEntity entity ) {
		StringBuffer s = new StringBuffer( "" );
		
		try {
			InputStream is = entity.getContent();
			long contentLen = entity.getContentLength();
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader in = new BufferedReader( reader, (int)contentLen );
			String str = "";
			
			try {
				do {
					str = in.readLine();
					
					if ( str != null ) {
						s.append( str );
					}
					else {
						break;
					}
				}
				while ( in.ready() );
				
				
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
			
			reader.close();
		}
		catch ( IllegalStateException e ) {
			e.printStackTrace();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	    
	    return s.toString();
	}
	
	private String getBodyInternal( HttpResponse response ) {
		StringBuffer s = new StringBuffer( "" );
		
		try {
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			long contentLen = entity.getContentLength();
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader in = new BufferedReader( reader, (int)contentLen );
			String str = "";
			
			try {
				do {
					str = in.readLine();
					
					if ( str != null ) {
						s.append( str );
					}
					else {
						break;
					}
				}
				while ( in.ready() );
				
				
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
			
			reader.close();
		}
		catch ( IllegalStateException e ) {
			e.printStackTrace();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	    
	    return s.toString();
	}
	
	public int getLastStatus() {
		return mStatus;
	}
	
	public String getBody() {
		return mBody;
	}
	
	public String getCookie() {
		String cookie = null;
		
		if ( Util.isValid( mCookie ) ) {
			for ( int i = 0; i < mCookie.length(); i ++ ) {
				if ( mCookie.charAt( i ) == ';' ) {
					cookie = String.copyValueOf( mCookie.toCharArray(), 0, i );
					break;
				}
			}
		}
		
		return cookie;
	}
	
	private HashMap<String, String> getHeaderMapInternal( HttpResponse response ) {
		Header[] header = response.getAllHeaders();
        
	    HashMap<String, String> headerMap = new HashMap<String, String>();
	    
	    for (int i = 0; i < header.length; i++) {
	    	headerMap.put( header[i].getName(), header[i].getValue() );
	    	
	    	if ( header[i].getName().equals( "Set-Cookie" ) ) {
	    		mCookie = header[i].getValue();
	    	}
	    }
	    
	    return headerMap;
	}
	
	public HashMap<String, String> getHeaderMap() {
		return mHeader;
	}

	public void post( String url, String entity ) {
		send( url, entity, false );
	}
}

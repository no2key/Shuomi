package com.android.shuomi.sina;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.persistence.Preference;
import com.android.shuomi.util.EventIndicator;
import com.weibo.net.AccessToken;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.AsyncWeiboRunner.RequestListener;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SinaWeiboShareView extends LinearLayout implements RequestListener
{
	private static final String TAG = "SinaWeiboShareView";
	
	public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
	public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_TOKEN_SECRET = "com.weibo.android.token.secret";
	public static final String EXTRA_WEBPAGE_URI = "com.weibo.android.webpage.uri";
	
	public static final int WEIBO_MAX_LENGTH = 140;
	
	private static final String  SHORT_URL_KEY = "url_short";
	
	private String mPicPath = "";
	private String mContent = "";
	private String mUri = "";
	private String mAccessToken = "";
	private String mTokenSecret = "";
	private String mResultContent = "";
	
	private Button mSend;
	private EditText mEdit;
	private TextView mTextNum;
	private FrameLayout mPiclayout;
	
	public SinaWeiboShareView( Context context, Bundle bundle ) 
	{
		super( context);

		mPicPath = bundle.getString( EXTRA_PIC_URI );
		mContent = bundle.getString( EXTRA_WEIBO_CONTENT );
		mUri = bundle.getString( EXTRA_WEBPAGE_URI );
		mAccessToken = bundle.getString( EXTRA_ACCESS_TOKEN );
		mTokenSecret = bundle.getString( EXTRA_TOKEN_SECRET );
		
		inflateLayout();
		preload();
	}
	
	private void inflateLayout()
	{
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.share_mblog_view, this, true );
	}
	
	private int checkContentExceeding()
	{
		int length = 0;
		
		if ( !TextUtils.isEmpty( mUri ) )
		{
			if ( mContent != null )
			{
				length += mContent.length() + 1;
			}
			
			length += ( mUri.length() - WEIBO_MAX_LENGTH );
		}
		
		Log.d( TAG, "exceeding char: " + length );
		
		return length;
	}
	
	private void preload()
	{
		if ( checkContentExceeding() > 0 )
		{
			getShortUrl();
		}
		else 
		{
			mResultContent = mContent + " " + mUri;
			setupWidget();
		}
	}
	
	private void getShortUrl()
	{
		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage( Message msg ) 
			{
				String param = ( String ) msg.obj;
				
				if ( param != null && param.equals( "done" ) ) 
				{
					setupWidget();
				}
			}
		};
		
		new Thread() 
		{
			public void run() 
			{
				String shortUri = extractShortUrl( requestShortUrl() );
				setupContent( shortUri );
				handler.sendMessage( handler.obtainMessage( 0, "done" ) );
			};
		}.start();
	}
	
	private String requestShortUrl()
	{
		String response = null;
		
		if ( !TextUtils.isEmpty( mUri ) )
		{
			String reqStr = "http://api.t.sina.com.cn/short_url/shorten.json?source=" + 
							SinaWeiboAuthView.CONSUMER_KEY + "&url_long=" + mUri;
			Uri reqUri = Uri.parse( reqStr );
			
			try 
			{
				response = Utility.openUrl( getContext(), reqUri.toString(), "GET", new WeiboParameters(), null );
				Log.d( TAG, "response = " + response );
			} 
			catch (WeiboException e) 
			{
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	private String extractShortUrl( String response )
	{
		String shortUri = null;
		
		if ( !TextUtils.isEmpty( response ) )
		{
			try 
			{
				JSONArray rspJsonArray = new JSONArray( response );
				
				if ( rspJsonArray.length() > 0 )
				{
					shortUri = rspJsonArray.getJSONObject(0).getString( SHORT_URL_KEY );
					Log.d( TAG, "short url: " + shortUri );
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		return shortUri;
	}
	
	private void setupContent( String shortUri )
	{
		String resultUri = TextUtils.isEmpty( shortUri ) ? mUri : shortUri;
		
		// extra one character is for the delimiter space
		if ( mContent.length() + resultUri.length() + 1 > WEIBO_MAX_LENGTH )
		{
			mResultContent = mContent.substring( 0, WEIBO_MAX_LENGTH - resultUri.length() - 1 );
		}
		else
		{
			mResultContent = mContent;
		}
		
		mResultContent += " " + resultUri;
	}
	
	private void setupWidget()
	{
		findViewById( R.id.empty_view_loading ).setVisibility( View.GONE );
		AccessToken accessToken = new AccessToken( mAccessToken, mTokenSecret );
		Weibo weibo = Weibo.getInstance();
		weibo.setAccessToken( accessToken );
		
		Button close = ( Button ) this.findViewById( R.id.btnClose );
		close.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//( ( ServiceListView ) getContext() ).goToPrevView();
				clearAccessToken();
				( ( ServiceListView ) getContext() ).goToNextView( new SinaWeiboAuthView( getContext(), mContent, mUri, mPicPath ), true );
			}
		});
		
		mSend = ( Button )this.findViewById( R.id.btnSend );
		mSend.setOnClickListener( new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				sendWeibo();
			}
		});
		
		
		LinearLayout total = ( LinearLayout ) this.findViewById( R.id.ll_text_limit_unit );
		total.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				displayClearTextAlert();
			}
		});
		
		
		ImageView picture = ( ImageView ) this.findViewById( R.id.ivDelPic );
		picture.setOnClickListener( new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				displayDeleteImageAlert();
			}
		});
		
		setupEditWidget();
		
		setupPictureLayout();
	}
	
	private void setupEditWidget()
	{
		mTextNum = ( TextView ) findViewById( R.id.tv_text_limit );
		mEdit = ( EditText ) findViewById( R.id.etEdit );
		mEdit.addTextChangedListener( new TextWatcher() 
		{
			public void afterTextChanged( Editable s ) {}
			public void beforeTextChanged( CharSequence s, int start, int count, int after ) {}

			public void onTextChanged( CharSequence s, int start, int before, int count )
			{
				int len = mEdit.getText().toString().length();
				
				if ( len <= WEIBO_MAX_LENGTH ) 
				{
					len = WEIBO_MAX_LENGTH - len;
					mTextNum.setTextColor( R.color.text_num_gray );
					
					if ( !mSend.isEnabled() )
					{
						mSend.setEnabled(true);
					}
				}
				else 
				{
					len = len - WEIBO_MAX_LENGTH;
					mTextNum.setTextColor( Color.RED );
					
					if ( mSend.isEnabled() )
					{
						mSend.setEnabled( false );
					}
				}
				
				mTextNum.setText(String.valueOf(len));
			}
		});
		
		mEdit.setText( mResultContent );
	}
	
	
	private void setupPictureLayout()
	{
		mPiclayout = ( FrameLayout ) this.findViewById( R.id.flPic );
		
		if ( TextUtils.isEmpty( this.mPicPath ) ) 
		{
			mPiclayout.setVisibility( View.GONE );
		}
		else
		{
			mPiclayout.setVisibility( View.VISIBLE );
			File file = getContext().getFileStreamPath( mPicPath );
			
			if ( file.exists() )
			{
				Bitmap pic = BitmapFactory.decodeFile( file.getAbsolutePath() );
				
				if ( pic != null )
				{
					ImageView image = (ImageView) this.findViewById( R.id.ivImage );
					image.setImageBitmap( pic );
				}
				else
				{
					Log.e( TAG, "image decode error" );
					mPiclayout.setVisibility( View.GONE );
				}
			}
			else
			{
				Log.e( TAG, "file not exist" );
				mPiclayout.setVisibility( View.GONE );
			}	
		}
	}
	
	private void displayDeleteImageAlert()
	{
		Dialog dialog = new AlertDialog.Builder( getContext() )
			.setTitle( R.string.attention )
			.setMessage( R.string.del_pic )
			.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					mPiclayout.setVisibility(View.GONE);
					mPicPath = "";
				}
			})
			.setNegativeButton(R.string.cancel, null)
			.create();
		
		dialog.show();
	}
	
	private void displayClearTextAlert()
	{
		Dialog dialog = new AlertDialog.Builder( getContext() )
			.setTitle( R.string.attention )
			.setMessage( R.string.delete_all )
			.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() 
			{
				public void onClick( DialogInterface dialog, int which ) 
				{
					mEdit.setText("");
				}
			})
			.setNegativeButton( R.string.cancel, null )
			.create();
		
		dialog.show();
	}
	
	private void clearAccessToken()
	{
		SharedPreferences pref = getContext().getSharedPreferences ( "groupon", 0 );
		Editor editor = pref.edit();
		editor.remove( Preference.OAUTH_TOKEN );
		editor.remove( Preference.OAUTH_TOKEN_SECRET );
		editor.commit();
	}
	
	private void sendWeibo()
	{
		Weibo weibo = Weibo.getInstance();
		
		try 
		{
			if ( ! TextUtils.isEmpty( ( String )( weibo.getAccessToken().getToken() ) ) )
			{
				String blogContent = mEdit.getText().toString();
				
				if ( TextUtils.isEmpty( blogContent ) )
				{
					EventIndicator.showToast( getContext(), R.string.weibo_no_text );
				}
				else
				{
					if( !TextUtils.isEmpty( mPicPath ) )
					{
						upload( weibo, Weibo.APP_KEY, mPicPath, blogContent, "", "" );
					}
					else
					{
						update( weibo, Weibo.APP_KEY, blogContent, "", "" );						
					}
				}
			}
			else
			{
				EventIndicator.showToast( getContext(), R.string.please_login );
			}
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (WeiboException e) 
		{
			e.printStackTrace();
		}
	}
	
	private WeiboParameters createParam( String source, String file, String status, String lon, String lat )
	{
		WeiboParameters bundle = new WeiboParameters();
		bundle.add( "source", source );		
		bundle.add( "status", status );
		
		if ( !TextUtils.isEmpty( file ) )
		{
			File picFile = getContext().getFileStreamPath( file );
			
			if ( picFile.exists() )
			{
				bundle.add( "pic", picFile.getAbsolutePath() );
			}
		}
				
		if ( !TextUtils.isEmpty( lon ) )
		{
			bundle.add("lon", lon);
		}
		
		if( !TextUtils.isEmpty( lat ) )
		{
			bundle.add("lat", lat);
		}
		
		return bundle;
	}
	
	private void upload( Weibo weibo, String source, String file, String status, String lon, String lat ) throws WeiboException
	{
		WeiboParameters bundle = createParam( source, file, status, lon, lat );
		
		String  url = Weibo.SERVER + "statuses/upload.json";
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner( weibo );
		weiboRunner.request( getContext(), url, bundle, Utility.HTTPMETHOD_POST, this );
	}

	private void update( Weibo weibo, String source, String status, String lon, String lat ) throws MalformedURLException, IOException, WeiboException
	{
		WeiboParameters bundle = createParam( source, null, status, lon, lat );
		
		String url = Weibo.SERVER + "statuses/update.json";
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner( weibo );
		weiboRunner.request( getContext(), url, bundle, Utility.HTTPMETHOD_POST, this );
	}

	@Override
	public void onComplete(String response) 
	{
		EventIndicator.showToast( getContext(), R.string.send_sucess );
		( ( ServiceListView ) getContext() ).goToPrevView();
	}

	@Override
	public void onIOException(IOException e) {}

	@Override
	public void onError(WeiboException e) 
	{
		String prompt = String.format( getContext().getString( R.string.send_failed ) + ":%s", e.getMessage() );
		EventIndicator.showToast( getContext(), prompt );				
	}
}

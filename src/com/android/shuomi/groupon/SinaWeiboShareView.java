package com.android.shuomi.groupon;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Toast;

public class SinaWeiboShareView extends LinearLayout implements RequestListener
{
	private static final String TAG = "SinaWeiboShareView";
	
	public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
	public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_TOKEN_SECRET = "com.weibo.android.token.secret";
	
	public static final int WEIBO_MAX_LENGTH = 140;
	
	private String mPicPath = "";
	private String mContent = "";
	
	private Button mSend;
	private EditText mEdit;
	private TextView mTextNum;
	private FrameLayout mPiclayout;
	
	public SinaWeiboShareView( Context context, Bundle bundle ) 
	{
		super( context);
		inflateLayout();
		setupWidget( bundle );
	}
	
	private void inflateLayout()
	{
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.share_mblog_view, this, true );
	}
	
	private void setupWidget( Bundle bundle )
	{
		mPicPath = bundle.getString( EXTRA_PIC_URI );
		mContent = bundle.getString( EXTRA_WEIBO_CONTENT );
		
		Log.d( TAG, "content: " + mContent );
		Log.d( TAG, "picPath: " + mPicPath );
		
		String mAccessToken = bundle.getString( EXTRA_ACCESS_TOKEN );
		String mTokenSecret = bundle.getString( EXTRA_TOKEN_SECRET );
		
		AccessToken accessToken = new AccessToken( mAccessToken, mTokenSecret );
		Weibo weibo = Weibo.getInstance();
		weibo.setAccessToken(accessToken);
		
		Button close = ( Button ) this.findViewById( R.id.btnClose );
		close.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				( ( ServiceListView ) getContext() ).goToPrevView();
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
				displayClearAlert();
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
		
		mEdit.setText( mContent );
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
			mPiclayout.setVisibility(View.VISIBLE);
			
			//File file = new File(mPicPath);
			File file = getContext().getFileStreamPath( mPicPath );
			
			if ( file.exists() )
			{
				Bitmap pic = BitmapFactory.decodeFile( this.mPicPath );
				ImageView image = (ImageView) this.findViewById( R.id.ivImage );
				image.setImageBitmap( pic );
			}
			else
			{
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
				}
			})
			.setNegativeButton(R.string.cancel, null)
			.create();
		
		dialog.show();
	}
	
	private void displayClearAlert()
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
	
	private void sendWeibo()
	{
		Weibo weibo = Weibo.getInstance();
		
		try 
		{
			if ( ! TextUtils.isEmpty( ( String )( weibo.getAccessToken().getToken() ) ) ) 
			{
				if( !TextUtils.isEmpty( mPicPath ) )
				{
					upload( weibo, Weibo.APP_KEY, mPicPath, mContent, "", "" );
				}
				else
				{
					update( weibo, Weibo.APP_KEY, mContent, "", "" );						
				}
			}
			else
			{
				Toast.makeText( getContext(), getContext().getString( R.string.please_login ), Toast.LENGTH_LONG );
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
			//File picFile = new File( file );
			File picFile = getContext().getFileStreamPath( file );
			
			if ( picFile.exists() )
			{
				bundle.add( "pic", file );
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
		Toast.makeText( getContext(), R.string.send_sucess, Toast.LENGTH_LONG );
		( ( ServiceListView ) getContext() ).goToPrevView();
	}

	@Override
	public void onIOException(IOException e) {}

	@Override
	public void onError(WeiboException e) 
	{
		Toast.makeText( getContext(), String.format( getContext().getString( R.string.send_failed ) + ":%s", e.getMessage() ), Toast.LENGTH_LONG );				
	}
}

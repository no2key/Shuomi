package com.android.shuomi;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

public class StreamReader 
{
	static final private String TAG = "StreamReader";
	static final public int DEFAULT_BUF_SIZE = 1024 * 8;

	private ByteArrayBuffer mByteBuffer = new ByteArrayBuffer(0);
	
	public StreamReader( InputStream is, int bufferSize )
	{
		read( is, bufferSize );
	}
	
	private void read( InputStream is, int bufferSize )
	{
		if ( is != null )
		{
			if ( bufferSize < 1 )
			{
				bufferSize = DEFAULT_BUF_SIZE;
			}
			
			try 
			{
				readStream( is, bufferSize );
			} 
			catch (IOException e) 
			{
				Log.d( TAG, "exception on read input stream" );
			}
		}
	}
	
	private void readStream( InputStream is, int bufferSize ) throws IOException
	{
		byte[] recvBuf = new byte[bufferSize];
		int length = 0;
		int total = 0;

		do 
		{
			while ( total < bufferSize )
			{
				length = is.read( recvBuf, total, bufferSize - total );
				
				if ( length < 0 ) break;
				
				total += length;
				//Log.d( TAG, "read: " + length + ", total: " + total );
			}
			
			mByteBuffer.append( recvBuf, 0, total );
			
			total = 0;
		} 
		while ( length > -1 );
	}
	
	public int byteSize()
	{
		return mByteBuffer.length();
	}
	
	public byte[] getBytes()
	{
		return mByteBuffer.toByteArray();
	}
}

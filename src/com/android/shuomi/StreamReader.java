package com.android.shuomi;

import java.io.IOException;
import java.io.InputStream;

public class StreamReader 
{
	static final public int DEFAULT_BUF_SIZE = 1024 * 8;
	
	private StringBuffer mStringBuffer = new StringBuffer();	
	
//	private InputStream mInputStream = null;
//	private int mBufferSize = 0;
	
	public StreamReader( InputStream is, int bufferSize )
	{
//		mInputStream = is;
//		mBufferSize = ( bufferSize > 0 ) ? bufferSize : DEFAULT_BUF_SIZE;
		
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				//Log.d( "HttpService", "read: " + length + ", total: " + total );
			}
			
			int last;
			for ( last = total-1; last > -1 && ( recvBuf[last] > 127 || recvBuf[last] < 0 ); last -- );
			
			String chunk = new String( recvBuf, 0, last + 1, "utf-8" );
			mStringBuffer.append( chunk );
			
			total = total - last - 1;
				
			for ( int i = 0; i < total; i ++ )
			{
				recvBuf[i] = recvBuf[last+1+i];
			}
		} 
		while ( length > -1 );
	}
	
	public int size()
	{
		return mStringBuffer.length();
	}
	
	public String getString()
	{
		return mStringBuffer.toString();
	}
}

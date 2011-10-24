package com.android.shuomi.persistence;

import java.util.ArrayList;
import java.util.Observer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class ServiceDb extends SQLiteOpenHelper {

	private static final String TAG = "ServiceDb";
	private static final String DATABASE_NAME = "shuomi_service.db";
	private static final int DATABASE_VERSION = 1;
	private static final String _ID = BaseColumns._ID;
	
	private Cursor mCursor;
	private boolean mIsTableExist = false;
	private String mTableName = null;
	private String[] mTableColumns = null;
	private DbObservable mAddRecordObservable = new DbObservable();
	
	public void registerRecordAddedObserver( Observer observer ) {
		mAddRecordObservable.addObserver(observer);
	}
	
	private void notifyRecordAddedObservers( Object data ) {
		mAddRecordObservable.notifyObservers( data );
	}
	
	public ServiceDb( Context context ) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}
	
	public void init( String tableName, String[] columns ) {
		mTableName = tableName;
		mTableColumns = columns;
		mCursor = null;		
		mIsTableExist = isTableExist( mTableName );
		
		if ( mIsTableExist ) {
			checkColumns( mTableName, mTableColumns );
		}
		else {
			Log.w( TAG, "table not exist" );
			createTable( getWritableDatabase(), mTableName, mTableColumns );
		}
	}
	
	private boolean isTableExist( String tabName ){
        boolean result = false;
        
        if ( tabName != null ) {
            try {
            	SQLiteDatabase db = getReadableDatabase();
                String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
                Cursor cursor = db.rawQuery( sql, null );
                if ( cursor.moveToNext() ) {
                	result = ( cursor.getInt(0) > 0 );
                }
                cursor.close();
            }
            catch ( Exception e ) {
            	Log.e( TAG, e.getMessage() );
            }
        }
                        
        return result;
	}
	
	private boolean checkColumns( String tableName, String[] columns ) {
		boolean result = true;
		SQLiteDatabase db = getWritableDatabase();
		
		if ( db != null && tableName != null && columns != null ) {
			Cursor cursor = db.query( tableName, null, null, null, null, null, null );
			for ( int i = 0; i < columns.length; i ++ ) {
				if ( cursor.getColumnIndex( columns[i] ) == -1 ) {
					if ( !addTableColumn( tableName, columns[i] ) ) {
						result = false;
						break;
					}
				}
			}
			cursor.close();
		}
		
		return result;
	}
	
	private boolean addTableColumn( String tableName, String columnName ) {
		boolean result = true;
		SQLiteDatabase db = getWritableDatabase();
		
		if ( db != null && tableName != null && columnName != null ) {
			String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + ";";
			try {
				db.execSQL( sql );
			}
			catch ( SQLException e ) {
				Log.e( TAG, e.getMessage() );
				result = false;
			}
		}
		
		Log.d( TAG, "add new column: " + tableName + " " + columnName );
		
		return result;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable( db, mTableName, mTableColumns );
	}
	
	private void createTable( SQLiteDatabase db, String name, String[] columns ) {
		if ( name != null && columns != null ) {
			Log.d( TAG, "createTable " + name );
			String sql = "CREATE TABLE IF NOT EXISTS " + name + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
			
			for ( int i = 0; i < columns.length; i ++ ) {
				sql += columns[i] + " TEXT NOT NULL";
				if ( i < columns.length - 1 ) {
					sql += ", ";
				}
			}
			
			sql += ");";
			 
			db.execSQL(sql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		upgradeTable( db, mTableName, mTableColumns );
	}
	
	private void upgradeTable( SQLiteDatabase db, String name, String[] columns ) {
		if ( name.length() > 0 ) {
			db.execSQL( "DROP TABLE IF EXISTS " + name );
			createTable( db, name, columns );
		}
	}
	
	public boolean insertRecord( String name, String[] columns, String[] fields ) {
		init( name, columns );
		
		boolean result = ( columns.length == fields.length );
		
		if ( result ) {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			
			for ( int i = 0; i < fields.length; i ++ ) {
				values.put( columns[i], fields[i] );
			}
			
			try {
				if ( db.insertOrThrow( name, null, values ) != -1 ) {
					notifyRecordAddedObservers( null );
				}
			}
			catch ( SQLException e ) {
				Log.w( TAG, e.getMessage() );
				result = false;
			}		
		}
		
		return result;
	}
	
//	public boolean insertRecords( String tableName, JSONObject dataObj ) {
//		boolean result = true;
//		
//		if ( dataObj != null && dataObj.length() > 0 ) {
//			Iterator<?> iter = dataObj.keys();
//			
//			String[] columns = new String[dataObj.length()];
//			String[] values = new String[dataObj.length()];
//			int i = 0;
//			
//			while( iter.hasNext() ) {
//		        String key = iter.next().toString();
//		        
//		        if ( Util.isValid( key ) ) {
//		        	columns[i] = key;
//			        try {
//						values[i] = dataObj.getString( columns[i] );
//					} 
//			        catch (JSONException e) {
//						Log.e( TAG, e.getMessage() );
//						result = false;
//						break;
//					}
//			        
//			        if ( columns[i].equals( "index" ) ) {
//			        	columns[i] = "_index_";
//			        }
//			        i ++;
//		        }
//		    }
//			
//			insertRecord( tableName, columns, values );
//		}
//		
//		return result;
//	}
	
	private Cursor query( String name, String[] columns, String orderByColumn ) {
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor cursor = null;
		
		if ( isTableExist( name ) ) {
			cursor = db.query( name, columns, null, null, null, null, orderByColumn );
			
			if ( cursor != null && cursor.getCount() > 0 ) {
				cursor.moveToFirst();
			}
		}
		
		return cursor;
	}
	
	private Cursor query( String name, String[] columns, int sortIndex ) {		
		String order = columns[sortIndex];
		
		return query( name, columns, order );
	}
	
	private ArrayList<String[]> traverseCursor( Cursor cursor ) {
		ArrayList<String[]> list = null;
		
		if ( cursor != null ) {
			list = new ArrayList<String[]>();
			
			boolean skipFirst = ( cursor.getColumnName( 0 ).equals( _ID ) );
			int columnCount = skipFirst ? cursor.getColumnCount() -1 : cursor.getColumnCount();
			int offset = skipFirst ? 1 : 0;
			
			for ( int i = 0; i < cursor.getCount(); i ++ ) {				
				String[] output = new String[columnCount];
				
				for ( int j = 0; j < columnCount; j ++ ) {
					output[j] = cursor.getString( j + offset );
				}
				
				list.add( output );
				cursor.moveToNext();
			}
			
			cursor.close();
		}
		
		return list;
	}
	
	public ArrayList<String[]> loadRecords( String tableName, String[] columns, String orderByColumn ) {
		return traverseCursor( query( tableName, columns, orderByColumn ) );
	}
	
	public ArrayList<String[]> loadRecords( String tableName, String[] columns, int sortIndex ) {
		return traverseCursor( query( tableName, columns, sortIndex ) );
	}
	
	public ArrayList<String[]> loadAllRecords( String tableName, String orderByColumn ) {
		return loadRecords( tableName, null, orderByColumn );
	}
	
	public ArrayList<String[]> loadAllRecords( String tableName, int sortIndex ) {
		return loadRecords( tableName, null, sortIndex );
	}
	
	public void clear( String tableName ) {
		if ( isTableExist( tableName ) ) {
			SQLiteDatabase db = getWritableDatabase();
			db.delete( tableName, "1", null );
		}
	}
	
	public void delete( String tableName ) {
		getWritableDatabase().execSQL( "DROP TABLE IF EXISTS " + tableName );
	}
	
	public void close() {
		if ( mCursor != null ) {
			mCursor.close();
		}
		
		super.close();
	}
	
//	public void update( String table, String id, String column, String value ) {
//		if ( isTableExist( table ) ) {
//			SQLiteDatabase db = getWritableDatabase();
//			ContentValues values = new ContentValues();
//			values.put( column, value );
//			db.update( table, values, "ID=?", new String[] {id} );
//		}
//	}
//	
//	public void update( String table, String id, String[] columns, String[] values ) {
//		if ( isTableExist( table ) ) {
//			SQLiteDatabase db = getWritableDatabase();
//			ContentValues contentValue = new ContentValues();
//			int length = Util.min( columns.length , values.length );
//			for ( int i = 0; i < length; i ++ ) {
//				contentValue.put( columns[i], values[i] );
//			}
//			
//			if ( length > 0 ) {
//				db.update( table, contentValue, "ID=?", new String[] {id} );
//			}
//		}
//	}
}

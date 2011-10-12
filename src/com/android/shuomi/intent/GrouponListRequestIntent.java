package com.android.shuomi.intent;

public class GrouponListRequestIntent extends RequestIntent {
	
	public GrouponListRequestIntent() {
		super( REQUEST.LIST_GROUPON );
		setResponseAction( RESPONSE.HTTP_RESPONSE_GROUPON );
	}
	
	public void setProvince( String province ) {
		putExtra( REQUEST.PARAM_PROVINCE, province );
	}
	
	public String getProvince() {
		return getStringExtra( REQUEST.PARAM_PROVINCE );
	}
	
	public void setCity( String city ) {
		putExtra( REQUEST.PARAM_CITY, city );
	}
	
	public String getCity() {
		return getStringExtra( REQUEST.PARAM_CITY );
	}
	
	public void setCate( String cate ) {
		putExtra( REQUEST.PARAM_CATE, cate );
	}
	
	public String getCate() {
		return getStringExtra( REQUEST.PARAM_CATE );
	}
	
	public void setTitle( String title ) {
		putExtra( REQUEST.PARAM_TITLE, title );
	}
	
	public String getTitle() {
		return getStringExtra( REQUEST.PARAM_TITLE );
	}
	
	public void setPage( int page ) {
		putExtra( REQUEST.PARAM_PAGE, page );
	}
	
	public int getPage() {
		return getIntExtra( REQUEST.PARAM_PAGE, -1 );
	}
	
	public void setRow( String row ) {
		putExtra( REQUEST.PARAM_ROW, row );
	}
	
	public String getRow() {
		return getStringExtra( REQUEST.PARAM_ROW );
	}
	
	public void setOrder( String order ) {
		putExtra( REQUEST.PARAM_ORDER, order );
	}
	
	public String getOrder() {
		return getStringExtra( REQUEST.PARAM_ORDER );
	}
}

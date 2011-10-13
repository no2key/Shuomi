package com.android.shuomi.groupon;

import com.android.shuomi.R;
import com.android.shuomi.intent.REQUEST;

import android.content.Context;
import android.os.Bundle;

public class GrouponListView extends MultiItemListLayout {

	private String mReqAction = REQUEST.LIST_GROUPON;
	private Bundle mBundle = null;
	
	public GrouponListView( Context context, Bundle bundle ) {
		super(context);

		mBundle = bundle;
		setupTitle();
		requestGrouponList( mBundle );
	}
	
	private void setupTitle()	{
		setTitleText( mBundle.getString( REQUEST.PARAM_CATE ) );
		setButtonText( R.string.hide_pic );
	}

	@Override
	protected String getRequestAction() {
		return mReqAction;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.groupon_list;
	}
}

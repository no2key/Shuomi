package com.android.shuomi.around;

import android.view.View;

import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;

public interface SetupPopupViewInterface 
{
	public void setup( View popupView, MapView.LayoutParams popupViewLayout, OverlayItem item );
}

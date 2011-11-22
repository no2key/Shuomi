package com.android.shuomi.baidumap;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;

public class BaiduOverlay extends ItemizedOverlay<OverlayItem> 
{
	private List<OverlayItem> mItems = new ArrayList<OverlayItem>();
	private Drawable mMarker;
	private Context mContext;

	public BaiduOverlay( Drawable marker, Context context ) 
	{
		super( marker );
		
		this.mMarker = marker;
		this.mContext = context;
	}

	@Override
	protected OverlayItem createItem( int i ) 
	{
		return mItems.get( i );
	}

	@Override
	public int size() 
	{
		return mItems.size();
	}
	
	public void addItem( OverlayItem item ) 
	{
		if ( item != null )
		{
			mItems.add( item );
			populate();
		}
    }
	
	@Override
	public void draw( Canvas canvas, MapView mapView, boolean shadow )
	{
//		// Projection�ӿ�������Ļ��������;�γ������֮��ı任
//		Projection projection = mapView.getProjection(); 
//		for (int index = size() - 1; index >= 0; index--) { // ����mGeoList
//			OverlayItem overLayItem = getItem(index); // �õ�����������item
//
//			String title = overLayItem.getTitle();
//			// �Ѿ�γ�ȱ任�������MapView���Ͻǵ���Ļ��������
//			Point point = projection.toPixels(overLayItem.getPoint(), null); 
//
//			// ���ڴ˴�������Ļ��ƴ���
//			Paint paintText = new Paint();
//			paintText.setColor(Color.BLUE);
//			paintText.setTextSize(15);
//			canvas.drawText(title, point.x-30, point.y, paintText); // �����ı�
//		}

		super.draw( canvas, mapView, shadow );
		boundCenterBottom( mMarker );
	}
	
	@Override
	// ��������¼�
	protected boolean onTap( int i ) 
	{
		setFocus( mItems.get( i ) );

		// ��������λ��,��ʹ֮��ʾ
//		GeoPoint pt = mGeoList.get(i).getPoint();
//		ItemizedOverlayDemo.mMapView.updateViewLayout( ItemizedOverlayDemo.mPopView,
//                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//                		pt, MapView.LayoutParams.BOTTOM_CENTER));
//		ItemizedOverlayDemo.mPopView.setVisibility(View.VISIBLE);
//		Toast.makeText(this.mContext, mGeoList.get(i).getSnippet(),
//				Toast.LENGTH_SHORT).show();
		
		return true;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView view ) {
		
		// ��ȥ����������
		//ItemizedOverlayDemo.mPopView.setVisibility(View.GONE);
		
		return super.onTap( point, view );
	}

}

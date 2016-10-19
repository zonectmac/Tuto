package com.xinyanyuan.project.at15.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.xinyanyuan.project.at15.R;

public class ZoomControlsView extends LinearLayout implements OnClickListener {
	private ImageView inIv;// 放大按钮
	private ImageView outIv;// 缩小按钮
	private BaiduMap baiduMap;// 百度地图对象控制器
	private MapStatus mapStatus;// 百度地图状态
	private float minZoomLevel;// 地图最小级别
	private float maxZoomLevel;// 地图最大级别

	public ZoomControlsView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		init();
	}

	public ZoomControlsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 获取布局视图
		LinearLayout view = (LinearLayout) LayoutInflater.from(getContext())
				.inflate(R.layout.zoom_controls_in_out, null);
		// 获取放大按钮
		inIv = (ImageView) view.findViewById(R.id.iv_zoom_in);
		// 获取缩小按钮
		outIv = (ImageView) view.findViewById(R.id.iv_zoom_out);
		// 设置点击事件
		inIv.setOnClickListener(this);
		outIv.setOnClickListener(this);
		// 添加View
		addView(view);
	}

	@Override
	public void onClick(View v) {
		this.mapStatus = this.baiduMap.getMapStatus();// 获取地图状态
		switch (v.getId()) {
		case R.id.iv_zoom_in:
			// 改变地图状态
			this.baiduMap.setMapStatus(MapStatusUpdateFactory
					.zoomTo(mapStatus.zoom + 1));
			controlZoomShow();// 改变缩放按钮
			break;
		case R.id.iv_zoom_out:
			// 改变地图状态
			this.baiduMap.setMapStatus(MapStatusUpdateFactory
					.zoomTo(mapStatus.zoom - 1));
			controlZoomShow();// 改变缩放按钮
			break;
		default:
			break;
		}
		// 重新获取状态
		mapStatus = this.baiduMap.getMapStatus();
	}

	/**
	 * 设置Map视图
	 * 
	 * @param mapView
	 */
	public void setMapView(MapView mapView) {
		// 获取百度地图控制器
		this.baiduMap = mapView.getMap();
		// 设置地图手势事件
		this.baiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		// 获取百度地图最大最小级别
		maxZoomLevel = baiduMap.getMaxZoomLevel();
		minZoomLevel = baiduMap.getMinZoomLevel();
		controlZoomShow();// 改变缩放按钮
	}

	/**
	 * 控制缩放图标显示
	 */
	private void controlZoomShow() {
		// 获取当前地图状态
		float zoom = this.baiduMap.getMapStatus().zoom;
		// 如果当前状态大于等于地图的最大状态，则放大按钮则失效
		if (zoom >= maxZoomLevel) {
			inIv.setImageResource(R.drawable.zoomin_press);
			inIv.setEnabled(false);
		} else {
			inIv.setImageResource(R.drawable.zoomin_big);
			inIv.setEnabled(true);
		}

		// 如果当前状态小于等于地图的最小状态，则缩小按钮失效
		if (zoom <= minZoomLevel) {
			outIv.setImageResource(R.drawable.zoomout_press);
			outIv.setEnabled(false);
		} else {
			outIv.setImageResource(R.drawable.zoomout_small);
			outIv.setEnabled(true);
		}
	}

	/**
	 * 地图状态改变相关接口实现
	 */
	BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {

		/**
		 * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
		 * 
		 * @param status
		 *            地图状态改变开始时的地图状态
		 */
		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

		}

		/**
		 * 地图状态变化结束
		 * 
		 * @param status
		 *            地图状态改变结束时的地图状态
		 */
		@Override
		public void onMapStatusChangeFinish(MapStatus arg0) {

		}

		/**
		 * 地图状态变化中
		 * 
		 * @param status
		 *            当前地图状态
		 */
		@Override
		public void onMapStatusChange(MapStatus arg0) {
			controlZoomShow();
		}
	};
}

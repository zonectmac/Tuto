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
	private ImageView inIv;// �Ŵ�ť
	private ImageView outIv;// ��С��ť
	private BaiduMap baiduMap;// �ٶȵ�ͼ���������
	private MapStatus mapStatus;// �ٶȵ�ͼ״̬
	private float minZoomLevel;// ��ͼ��С����
	private float maxZoomLevel;// ��ͼ��󼶱�

	public ZoomControlsView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		init();
	}

	public ZoomControlsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		// ��ȡ������ͼ
		LinearLayout view = (LinearLayout) LayoutInflater.from(getContext())
				.inflate(R.layout.zoom_controls_in_out, null);
		// ��ȡ�Ŵ�ť
		inIv = (ImageView) view.findViewById(R.id.iv_zoom_in);
		// ��ȡ��С��ť
		outIv = (ImageView) view.findViewById(R.id.iv_zoom_out);
		// ���õ���¼�
		inIv.setOnClickListener(this);
		outIv.setOnClickListener(this);
		// ���View
		addView(view);
	}

	@Override
	public void onClick(View v) {
		this.mapStatus = this.baiduMap.getMapStatus();// ��ȡ��ͼ״̬
		switch (v.getId()) {
		case R.id.iv_zoom_in:
			// �ı��ͼ״̬
			this.baiduMap.setMapStatus(MapStatusUpdateFactory
					.zoomTo(mapStatus.zoom + 1));
			controlZoomShow();// �ı����Ű�ť
			break;
		case R.id.iv_zoom_out:
			// �ı��ͼ״̬
			this.baiduMap.setMapStatus(MapStatusUpdateFactory
					.zoomTo(mapStatus.zoom - 1));
			controlZoomShow();// �ı����Ű�ť
			break;
		default:
			break;
		}
		// ���»�ȡ״̬
		mapStatus = this.baiduMap.getMapStatus();
	}

	/**
	 * ����Map��ͼ
	 * 
	 * @param mapView
	 */
	public void setMapView(MapView mapView) {
		// ��ȡ�ٶȵ�ͼ������
		this.baiduMap = mapView.getMap();
		// ���õ�ͼ�����¼�
		this.baiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		// ��ȡ�ٶȵ�ͼ�����С����
		maxZoomLevel = baiduMap.getMaxZoomLevel();
		minZoomLevel = baiduMap.getMinZoomLevel();
		controlZoomShow();// �ı����Ű�ť
	}

	/**
	 * ��������ͼ����ʾ
	 */
	private void controlZoomShow() {
		// ��ȡ��ǰ��ͼ״̬
		float zoom = this.baiduMap.getMapStatus().zoom;
		// �����ǰ״̬���ڵ��ڵ�ͼ�����״̬����Ŵ�ť��ʧЧ
		if (zoom >= maxZoomLevel) {
			inIv.setImageResource(R.drawable.zoomin_press);
			inIv.setEnabled(false);
		} else {
			inIv.setImageResource(R.drawable.zoomin_big);
			inIv.setEnabled(true);
		}

		// �����ǰ״̬С�ڵ��ڵ�ͼ����С״̬������С��ťʧЧ
		if (zoom <= minZoomLevel) {
			outIv.setImageResource(R.drawable.zoomout_press);
			outIv.setEnabled(false);
		} else {
			outIv.setImageResource(R.drawable.zoomout_small);
			outIv.setEnabled(true);
		}
	}

	/**
	 * ��ͼ״̬�ı���ؽӿ�ʵ��
	 */
	BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {

		/**
		 * ���Ʋ�����ͼ�����õ�ͼ״̬�Ȳ������µ�ͼ״̬��ʼ�ı䡣
		 * 
		 * @param status
		 *            ��ͼ״̬�ı俪ʼʱ�ĵ�ͼ״̬
		 */
		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {

		}

		/**
		 * ��ͼ״̬�仯����
		 * 
		 * @param status
		 *            ��ͼ״̬�ı����ʱ�ĵ�ͼ״̬
		 */
		@Override
		public void onMapStatusChangeFinish(MapStatus arg0) {

		}

		/**
		 * ��ͼ״̬�仯��
		 * 
		 * @param status
		 *            ��ǰ��ͼ״̬
		 */
		@Override
		public void onMapStatusChange(MapStatus arg0) {
			controlZoomShow();
		}
	};
}

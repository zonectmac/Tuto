package com.xinyanyuan.project.at15.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.AlertView.Style;
import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.utils.BitmapLinUtils;
import com.xinyanyuan.project.at15.utils.BitmapLinUtils.SysooLin;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;
import com.xinyanyuan.project.at15.utils.ImageLoadHelper;
import com.xinyanyuan.project.at15.view.CircleImageView;
import com.xinyanyuan.project.at15.view.ZoomControlsView;

public class NearbyFragment extends Fragment implements OnClickListener {
	private View view;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocationClient;
	private ZoomControlsView zoomControlsView;
	public BDLocationListener myListener = new MyLocationListener();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_nearby, null);
		mLocationClient = new LocationClient(getActivity());
		mLocationClient.registerLocationListener(myListener); // ע���������
		initLocation();
		initView();
		return view;
	}

	private void initView() {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setBuildingsEnabled(true);// ������ʾ¥��
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(8f));// ���õ�ͼ״̬
		zoomControlsView = (ZoomControlsView) view
				.findViewById(R.id.zoomControlsView);
		zoomControlsView.setMapView(mMapView);
		view.findViewById(R.id.iv_centerPoint).setOnClickListener(this);
		view.findViewById(R.id.iv_saixuan).setOnClickListener(this);
	}

	private String[] others = { "500", "1000", "2000", "5000" };

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_centerPoint:
			mapCenterPoint(latitude, longitude);
			mLocationClient.start();// ��ʼ��λ
			break;
		case R.id.iv_saixuan:
			new AlertView("ѡ��Χ", null, "ȡ��", null, others, getActivity(),
					Style.ActionSheet, new OnItemClickListener() {

						@Override
						public void onItemClick(Object o, int position) {
							if (position < others.length) {
								getNearbyProgrammer(Integer
										.parseInt(others[position]));

							}

						}
					}).show();
			break;
		case R.id.civ_iconBD:

			break;

		}
	}

	private boolean isFirstLocation = true;// �Ƿ��һ�ζ�λ
	private double latitude = 0.0;
	private double longitude = 0.0;

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			String addrStr = location.getAddrStr();
			SysooLin.i("location" + addrStr);
			if (isFirstLocation) {
				mapCenterPoint(latitude, longitude);
				isFirstLocation = false;
			}
			if (App.getApp().isAllowFind()) {
				// ���������ύ��γ��
				openAllowFind(latitude, longitude);
			}
			// mLocationClient = new LocationClient(getActivity()); //
			// ����LocationClient��
			// mLocationClient.registerLocationListener(myListener); // ע���������
			initLocation();
		}

	}

	public void openAllowFind(double latitude, double longitude) {
		RequestParams params = new RequestParams();
		params.put("userId", App.getApp().getUser().getUserId());
		params.put("latitude", latitude);
		params.put("longitude", longitude);
		ConnUtils.post(ConnUtils.URL_OPEN_ALLOW_FIND, params, null);
	}

	int currentZoom = 15;// ��ͼ��ʾ����

	/**
	 * �ٶȵ�ͼ���õ�ͼ���ĵ�
	 * 
	 * @param la
	 * @param lo
	 */
	private void mapCenterPoint(double la, double lo) {
		// ���õ�ͼ����
		LatLng latlng = new LatLng(la, lo);
		// ��ͼ״̬
		MapStatus mapStatus = new MapStatus.Builder().target(latlng)
				.zoom(currentZoom).build();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));

		// ����Maker�����
		LatLng point = new LatLng(la, lo);
		// ����Markerͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_location4);
		// ����MarkerOption�������ڵ�ͼ�����Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// ����ק
		// .draggable(true)
		// ����marker���ڲ㼶
		// .zIndex(9);
		// �ڵ�ͼ�����Marker������ʾ
		if (centerMarker != null) {
			centerMarker.remove();
		}
		centerMarker = (Marker) mBaiduMap.addOverlay(option);
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
		option.setCoorType("bd09ll");// ��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
		int span = 3000;
		option.setScanSpan(span);// ��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
		option.setIsNeedAddress(true);// ��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
		option.setOpenGps(true);// ��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
		option.setLocationNotify(true);// ��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
		option.setIsNeedLocationDescribe(true);// ��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
		option.setIsNeedLocationPoiList(true);// ��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
		option.setIgnoreKillProcess(false);// ��ѡ��Ĭ��false����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ��ɱ��
		option.SetIgnoreCacheException(false);// ��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
		option.setEnableSimulateGps(false);// ��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	private Marker centerMarker = null;
	private List<Marker> listIcon = new ArrayList<Marker>();

	private void addUserIcon(double la, double lo, String url, String nikeName,
			String sex, final String userPhone) {
		SysooLin.i("addUserIcon la " + la);
		SysooLin.i("addUserIcon lo " + lo);
		SysooLin.i("addUserIcon url " + url);
		// ����Maker�����
		final LatLng point = new LatLng(la, lo);
		final View layout = View.inflate(getActivity(),
				R.layout.view_baidu_icon, null);
		final CircleImageView civ_iconBD = (CircleImageView) layout
				.findViewById(R.id.civ_iconBD);
		civ_iconBD.setOnClickListener(this);
		civ_iconBD.setBorderColor(sex.equals("��") ? Color.BLUE : Color.RED);
		TextView tv_nickName = (TextView) layout.findViewById(R.id.tv_nickName);
		tv_nickName.setText(nikeName);
		ImageLoadHelper.getInstance().loadImage(url,
				ImageLoadHelper.getDefaultFadeOptions(),
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						SysooLin.i("onLoadingStarted");
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						SysooLin.i("onLoadingFailed");

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						SysooLin.i("onLoadingComplete1");
						// ����Markerͼ��
						civ_iconBD.setImageBitmap(arg2);
						Bitmap convertViewToBitmap = BitmapLinUtils
								.convertViewToBitmap(layout);
						BitmapDescriptor bitmap = BitmapDescriptorFactory
								.fromBitmap(convertViewToBitmap);
						// ����MarkerOption�������ڵ�ͼ�����Marker
						OverlayOptions options = new MarkerOptions().position(
								point).icon(bitmap);
						// �ڵ�ͼ�����Marker������ʾ
						Marker iconMarker = (Marker) mBaiduMap
								.addOverlay(options);
						iconMarker.setTitle(userPhone);
						listIcon.add(iconMarker);
						SysooLin.i("onLoadingComplete2");
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						SysooLin.i("onLoadingCancelled");
					}
				});
	}

	private void clearMarkIcon() {
		if (listIcon == null) {
			return;
		}
		for (Marker marker : listIcon) {
			marker.remove();
		}
		listIcon.clear();
	}

	/**
	 * ��ȡ��������Գ
	 * 
	 * @param parseInt
	 */
	private void getNearbyProgrammer(int sweepRange) {
		clearMarkIcon();
		RequestParams params = new RequestParams();
		params.put("userId", App.getApp().getUser().getUserId());
		params.put("sweepRange", sweepRange);
		params.put("latitude", latitude);
		params.put("longitude", longitude);
		ConnUtils.post(ConnUtils.URL_GET_NEARBY_PROGRAMMER, params,
				new OnHttpCallBack() {

					@Override
					public void onOk(JSONObject json) {
						Toast.makeText(getActivity(),
								json.optString("message"), Toast.LENGTH_SHORT)
								.show();
						if (json.optInt("code") == 1) {
							JSONArray array = json.optJSONArray("list");
							for (int i = 0; i < array.length(); i++) {
								JSONObject obj = array.optJSONObject(i);

								double latitude = obj.optDouble("latitude");
								double longitude = obj.optDouble("longitude");
								userPhone = obj.optString("userPhone");
								nickName = obj.optString("nickName");
								String icon = obj.optString("icon");
								sex = obj.optString("sex");
								addUserIcon(latitude, longitude, icon,
										nickName, sex, userPhone);
							}
						}
					}

					@Override
					public void onFail(int code, String err) {

					}
				});
	}

	private String userPhone;
	private String nickName;
	private String sex;

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

}

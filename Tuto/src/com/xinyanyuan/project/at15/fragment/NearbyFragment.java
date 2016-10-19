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
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		initView();
		return view;
	}

	private void initView() {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setBuildingsEnabled(true);// 设置显示楼体
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(8f));// 设置地图状态
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
			mLocationClient.start();// 开始定位
			break;
		case R.id.iv_saixuan:
			new AlertView("选择范围", null, "取消", null, others, getActivity(),
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

	private boolean isFirstLocation = true;// 是否第一次定位
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
				// 往服务器提交经纬度
				openAllowFind(latitude, longitude);
			}
			// mLocationClient = new LocationClient(getActivity()); //
			// 声明LocationClient类
			// mLocationClient.registerLocationListener(myListener); // 注册监听函数
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

	int currentZoom = 15;// 地图显示比例

	/**
	 * 百度地图设置地图中心点
	 * 
	 * @param la
	 * @param lo
	 */
	private void mapCenterPoint(double la, double lo) {
		// 设置地图中心
		LatLng latlng = new LatLng(la, lo);
		// 地图状态
		MapStatus mapStatus = new MapStatus.Builder().target(latlng)
				.zoom(currentZoom).build();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));

		// 定义Maker坐标点
		LatLng point = new LatLng(la, lo);
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_location4);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// 可拖拽
		// .draggable(true)
		// 设置marker所在层级
		// .zIndex(9);
		// 在地图上添加Marker，并显示
		if (centerMarker != null) {
			centerMarker.remove();
		}
		centerMarker = (Marker) mBaiduMap.addOverlay(option);
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 3000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
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
		// 定义Maker坐标点
		final LatLng point = new LatLng(la, lo);
		final View layout = View.inflate(getActivity(),
				R.layout.view_baidu_icon, null);
		final CircleImageView civ_iconBD = (CircleImageView) layout
				.findViewById(R.id.civ_iconBD);
		civ_iconBD.setOnClickListener(this);
		civ_iconBD.setBorderColor(sex.equals("男") ? Color.BLUE : Color.RED);
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
						// 构建Marker图标
						civ_iconBD.setImageBitmap(arg2);
						Bitmap convertViewToBitmap = BitmapLinUtils
								.convertViewToBitmap(layout);
						BitmapDescriptor bitmap = BitmapDescriptorFactory
								.fromBitmap(convertViewToBitmap);
						// 构建MarkerOption，用于在地图上添加Marker
						OverlayOptions options = new MarkerOptions().position(
								point).icon(bitmap);
						// 在地图上添加Marker，并显示
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
	 * 获取附近程序猿
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
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

}

package com.tian.gaodemaplibrary;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;


/**
 * Created by tiantan on 16/6/19.
 */
public class MapLocationActivity extends Activity implements LocationSource, AMapLocationListener {

    private MapView map2DView;
    private AMap aMap;
    private OnLocationChangedListener onLocationChangedListener;
    private AMapLocationClient aMapLocationClient;
    private AMapLocationClientOption aMapLocationClientOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_location_activity);
        this.initView();
        this.initMapView();
        this.map2DView.onCreate(savedInstanceState);
        this.setMapAttributes();
        this.setLocationListeners();
    }

    private void initMapView() {
        if (this.aMap == null) {
            this.aMap = this.map2DView.getMap();
            this.setMapAttributes();
        }

    }

    private void setMapAttributes() {
        //自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //设置小蓝点的图标
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.location_marker);
        myLocationStyle.myLocationIcon(bitmapDescriptor);
        //设置圆形边框的颜色
        myLocationStyle.strokeColor(Color.GREEN);
        //设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));
        //设置小蓝点的锚点
        //myLocationStyle.anchor(int, int);
        //设置圆形边框的粗细
        myLocationStyle.strokeWidth(0.5f);
        this.aMap.setMyLocationStyle(myLocationStyle);
    }

    private void setLocationListeners() {
        aMap.setLocationSource(this);// 设置定位监听
        this.aMap.setLocationSource(this);
        // 设置默认定位按钮是否显示
        this.aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        this.aMap.setMyLocationEnabled(true);
    }

    private void initView() {
        this.map2DView = (MapView) findViewById(R.id.gaode_2d_map_view);

    }


    @Override
    protected void onResume() {
        super.onResume();
        this.map2DView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.map2DView.onPause();
        deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.map2DView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.map2DView.onSaveInstanceState(outState);
    }

    /**定位成功后回调函数*/
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (this.onLocationChangedListener != null && aMapLocation != null){
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS){
                // 显示系统小蓝点
                this.onLocationChangedListener.onLocationChanged(aMapLocation);
            }
            else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr 高德地图: ", errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener _onLocationChangedListener) {
        this.onLocationChangedListener = _onLocationChangedListener;
        if (aMapLocationClient == null){
            aMapLocationClient = new AMapLocationClient(this);
            aMapLocationClientOption = new AMapLocationClientOption();
            //设置定位监听
            aMapLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            //设置定位参数
            aMapLocationClient.setLocationOption(aMapLocationClientOption);

            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            aMapLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
        if (aMapLocationClient != null){
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
        }
        this.aMapLocationClient = null;
    }
}

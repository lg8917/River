package bzu.edu.cn.river.location;

import android.app.Activity;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import bzu.edu.cn.river.R;
public class Orientation extends Activity implements TencentLocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(20000)
                .setRequestLevel(Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                .setAllowCache(true);
        //开启定位监听器
        init(request);
    }

    public void init(TencentLocationRequest request){
        Context context = this;
        TencentLocationListener listener = this;
        TencentLocationManager locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(request, listener);
        if(error==0){
            Log.v("this", "注册位置监听器成功！");
        }else{
            Log.v("this", "注册位置监听器失败！");
        }
    }

  //  @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        // TODO Auto-generated method stub
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            Log.v("this", "定位成功！");
            if(location!=null){
                double lat=location.getLatitude();
                double lon=location.getLongitude();
                String nation=location.getNation();
                String province=location.getProvince();
                String city=location.getCity();
                String district=location.getDistrict();
                String town=location.getTown();
                String village=location.getVillage();
                String street=location.getStreet();
                String streetNo=location.getStreetNo();
                Intent in=getIntent();
//设置返回结果成功
                in.putExtra("city", city);
                in.putExtra("value",district+town+village);
                this.setResult(RESULT_OK, in);

//关闭当前activity
                finish();
               // textView.setText("当前经纬度："+lat+","+lon+nation+province+city+district+town+village+street+streetNo);
            }
        } else {
            // 定位失败
            Log.v("this", "定位失败！");
        }
    }

    @Override
    public void onStatusUpdate(String arg0, int arg1, String arg2) {
        // TODO Auto-generated method stub
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //关闭定位监听器
        TencentLocationManager locationManager =
                TencentLocationManager.getInstance(this);
        locationManager.removeUpdates(this);
    }

}

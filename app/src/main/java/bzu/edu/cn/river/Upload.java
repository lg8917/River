package bzu.edu.cn.river;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import bzu.edu.cn.river.adapter.MyListAdapter;
import bzu.edu.cn.river.bean.Warn;
import bzu.edu.cn.river.constant.UrlConst;
import bzu.edu.cn.river.utlis.AudioRecoderUtils;
import bzu.edu.cn.river.utlis.JsonResult;
import bzu.edu.cn.river.utlis.PopupWindowFactory;
import bzu.edu.cn.river.utlis.TimeUtils;
import cz.msebera.android.httpclient.Header;

/**
 * Created by LG on 2017/10/16.
 */

public class Upload extends AppCompatActivity implements View.OnClickListener, TencentLocationListener {
    static final int VOICE_REQUEST_CODE = 66;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    private Button pai;
    private Button ub3;
    private Button ub4;
    private ImageView mImageView;
    private TextView mTextView;
    private AudioRecoderUtils mAudioRecoderUtils;
    private Context context;
    private PopupWindowFactory mPop;
    private RelativeLayout rl;
    private ImageView ui1;
    private View inflate;
    private Dialog dialog;
    private TextView ut3;
    private TextView ut5;
    private TextView ut7;
    String picturePath;
    private Uri imageUri; //图片路径
    private String filename;
    private static int RESULT_LOAD_IMAGE = 1;
    private MediaRecorder mRecorder;
    private ArrayList mVoicesList;
    private String mFileName;
    private MyListAdapter mAdapter;
    private EditText ue1;
    private Warn warn;
    private File image;

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    RequestParams params = new RequestParams();


    File file1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.upload);
        pai = (Button) findViewById(R.id.ub1);
        rl = (RelativeLayout) findViewById(R.id.ur4);
        ui1 = (ImageView) findViewById(R.id.ui1);
        ut3 = (TextView) findViewById(R.id.ut3);
        ub3 = (Button) findViewById(R.id.ub3);
        ub4 = (Button) findViewById(R.id.ub4);
        ut7 = (TextView) findViewById(R.id.ut7);
        ut5 = (TextView) findViewById(R.id.ut5);
        ue1 = (EditText) findViewById(R.id.ue1);
        warn = new Warn();
        init();
        context = this;
        //PopupWindow的布局文件
        final View view = View.inflate(this, R.layout.layout_microphone, null);

        mPop = new PopupWindowFactory(this, view);

        //PopupWindow布局文件里面的控件
        mImageView = (ImageView) view.findViewById(R.id.iv_recording_icon);
        mTextView = (TextView) view.findViewById(R.id.tv_recording_time);

        mAudioRecoderUtils = new AudioRecoderUtils();

        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(TimeUtils.long2String(time));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {
                Toast.makeText(Upload.this, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
                mTextView.setText(TimeUtils.long2String(0));
            }
        });
        //6.0以上需要权限申请
        requestPermissions();
    }
    /**
     * 开启扫描之前判断权限是否打开
     */
    private void requestPermissions() {
        //判断是否开启摄像头权限
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                ) {
            StartListener();
            //判断是否开启语音权限
        } else {
            //请求获取摄像头权限
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, VOICE_REQUEST_CODE);
        }
    }
    /**
     * 请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VOICE_REQUEST_CODE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                StartListener();
            } else {
                Toast.makeText(context, "已拒绝权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void StartListener() {
        //Button的touch监听
        ub3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPop.showAtLocation(rl, Gravity.CENTER, 0, 0);
                        ub3.setText("松开保存");
                        mAudioRecoderUtils.startRecord();
                        break;
                    case MotionEvent.ACTION_UP:

                        mAudioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
//                        mAudioRecoderUtils.cancelRecord();    //取消录音（不保存录音文件）
                        mPop.dismiss();
                        ub3.setText("按住说话");

                        break;
                }
                return true;
            }
        });
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(20000)
                .setRequestLevel(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                .setAllowCache(true);
        //开启定位监听器
        init(request);
        //  ini();
    }

    public void init(TencentLocationRequest request) {
        Context context = this;
        TencentLocationListener listener = this;
        TencentLocationManager locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(request, listener);
        if (error == 0) {
            Log.v("this", "注册位置监听器成功！");
        } else {
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
            if (location != null) {
                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());
                String address = location.getAddress();
                ut3.setText(lat);
                ut5.setText(lon);
                ut7.setText(address);
//关闭当前activity

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
    public void on(View view) {
        finish();
    }
    public void init() {
        ui1.setOnClickListener(new addPhoto());
        pai.setOnClickListener(new addPhoto());
        ub4.setOnClickListener(new upload());
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(">>>>>>>>>>>" + requestCode + ">>" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();//获得图片的绝对路径
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            ui1.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            System.out.println("图片地址：" + picturePath);
          image=new File(picturePath);
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(Upload.this, "ActivityResult resultCode error", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == 0) {//第二个页面返回来的数据
//resultcode 区分结果是否属于正常返回
            // Toast.makeText(MainActivity.this,"跳转成功",Toast.LENGTH_LONG).show();
        }
    }
    private class addPhoto implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }
    public void onClick(View view) {
    }
    //上传至数据库功能
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void warnValues() {
        warn.setLatitude(Double.parseDouble(ut3.getText().toString()));
        warn.setLongitude(Double.parseDouble(ut5.getText().toString()));
        warn.setName(ut7.getText().toString());
//        warn.setUploader(upLoader);
        warn.setUploader("李根");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        warn.setCreateTime(df.format(new Date()));// new Date()为获取当前系统时间
        warn.setDescription(ue1.getText().toString());
        warn.setState(2);

    }
    private class upload implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            warnValues();
//            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//            RequestParams params = new RequestParams();
//                params.put("warn.warn",warn);
            params.put("warn.longitude", warn.getLongitude());
            params.put("warn.latitude", warn.getLatitude());
            params.put("warn.name ", warn.getName());

            params.put("warn.createTime", warn.getCreateTime());
            params.put("warn.description", warn.getDescription());
            params.put("warn.uploader", warn.getUploader());
            params.put("warn.state", warn.getState());

            file1=  new File(picturePath);
            try {
                params.put("warn.zhaopian", file1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            params.put("warn.file ", "fjsdijf.jpg");
//            params.put("warn.file", warn.getFile());
//            System.out.println(image);
//           try {
//              params.put("image",image);
//           } catch (FileNotFoundException e) {
//               e.printStackTrace();
//           }
            //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
            String url = UrlConst.SAVA_WARN;
            // Toast.makeText(LoginActivity.this,params.toString(),Toast.LENGTH_LONG).show();
            asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    //statusCode:状态码 s   headers：头信息  responseBody：返回的内容，返回的实体
                    //Toast.makeText(LoginActivity.this, "asdf", Toast.LENGTH_LONG).show();
                    //判断状态码
                    if (statusCode == 200) {
                        //获取结果
                        try {
                            String result = new String(responseBody, "utf-8");
                            //Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
                            Gson gson = new Gson();
                            JsonResult jsonResult = gson.fromJson(result, JsonResult.class);
                            //Toast.makeText(LoginActivity.this, jsonResult.getMessage(), Toast.LENGTH_LONG).show();
                            //2.判断返回的json数据
                            //2.1若返回json数据success为true的话，调用保存密码与自动登录状态的方法
                            if (jsonResult.isSuccess()) {   //2.1成功，则进入主界面
                                Toast.makeText(Upload.this, "添加成功", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Upload.this, Upload.class);
                                startActivity(intent);
                            } else {   //2.2失败则显示提示信息
                                new AlertDialog.Builder(Upload.this).setTitle("信息提示")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setMessage(jsonResult.getMessage())
                                        .setPositiveButton("确定", null)
                                        .setNegativeButton("取消", null)
                                        .create().show();
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                }
            });
        }
    }
}

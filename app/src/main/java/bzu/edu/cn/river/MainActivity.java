package bzu.edu.cn.river;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import bzu.edu.cn.river.constant.UrlConst;
import bzu.edu.cn.river.utlis.JsonResult;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
    }
    //注销登录按钮的点击事件处理
    public void btn_logout_click(View view){
        //1.调用网络进行登录
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        //url:   parmas：请求时携带的参数信息   responseHandler：是一个匿名内部类接受成功过失败
        String url = UrlConst.LOGOUT;
        asyncHttpClient.post(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }
            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {

            }

            public void onSuccess(int statusCode, PreferenceActivity.Header[] headers, byte[] responseBody) {
                //statusCode:状态码    headers：头信息  responseBody：返回的内容，返回的实体

                //判断状态码
                if(statusCode == 200){
                    //获取结果
                    try {
                        String result = new String(responseBody,"utf-8");
                        //Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
                        Gson gson = new Gson();
                        JsonResult jsonResult = gson.fromJson(result, JsonResult.class);
                        //Toast.makeText(LoginActivity.this, jsonResult.getMessage(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(MainActivity.this, Login.class);
                        SharedPreferences sp = MainActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("autoLogin", false);
                        editor.commit();

                        startActivity(intent);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            public void onFailure(int statusCode, PreferenceActivity.Header[] headers,
                                  byte[] responseBody, Throwable error) {
            }
        });
    }
}




package bzu.edu.cn.river;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by LG on 2017/10/18.
 */
public class History extends AppCompatActivity {
    private Button hb;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
       hb = (Button) findViewById(R.id.hb1);
        hb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(History.this, Upload.class);
                History.this.startActivity(intent);
            }
        });
    }

}
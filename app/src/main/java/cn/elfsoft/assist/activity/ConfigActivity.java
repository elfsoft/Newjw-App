package cn.elfsoft.assist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import cn.elfsoft.assist.R;
import cn.elfsoft.assist.loginactivity.InitLoginActivity;
import cn.elfsoft.assist.util.Config;

public class ConfigActivity extends Activity {
    private long seconds = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String phone = Config.getSettings(getApplicationContext(), "elfphone");
                Intent intent = null;
                if (phone.equals("")) {
                    intent = new Intent(ConfigActivity.this, InitLoginActivity.class);
                } else {
                    intent = new Intent(ConfigActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }

        }, seconds);
    }
}
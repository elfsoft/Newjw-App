package cn.elfsoft.assist.loginactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.activity.MainActivity;
import cn.elfsoft.assist.logic.LoginCent;

public class SmsRegistActivity extends Activity {
    private EditText name, pw, pw2;
    private LoginCent cent;
    private String phone;
    private LinearLayout wait;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 22) {
                Toast.makeText(getApplicationContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                wait.setVisibility(View.INVISIBLE);
            }
            if (msg.what == 11) {
                wait.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                Config.setSettings(getApplicationContext(), "elfphone", phone);
                Config.setSettings(getApplicationContext(), "elfpw", pw.getText().toString().trim());
                Config.setSettings(getApplicationContext(), "elfname", name.getText().toString().trim());
                finish();
                Intent intent = new Intent(SmsRegistActivity.this, MainActivity.class);
                startActivity(intent);
            }
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
                wait.setVisibility(View.INVISIBLE);
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cent = new LoginCent();
        setContentView(R.layout.activity_regist);
        phone = getIntent().getStringExtra("phone");
//		phone = "18742038812";
        initUI();
    }

    private void initUI() {
        wait = (LinearLayout) findViewById(R.id.waitbar);
        name = (EditText) findViewById(R.id.name);
        pw = (EditText) findViewById(R.id.pw);
        pw2 = (EditText) findViewById(R.id.pw2);
    }

    public void regist(View v) {
        wait.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                String usr = name.getText().toString().trim();
                String psd = pw.getText().toString().trim();
                String psd2 = pw2.getText().toString().trim();
                if (!psd.equals(psd2)) {
                    handler.sendEmptyMessage(22);
                } else {
                    try {
                        boolean bool = cent.regist(phone, usr, psd);
                        if (bool) {
                            handler.sendEmptyMessage(11);
                        }
                    } catch (Exception e) {
                        handler.sendEmptyMessage(0);
                        e.printStackTrace();
                    }
                }
            }

            ;
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, InitLoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}

package cn.elfsoft.assist.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.logic.HttpCent;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText username, password, code;
    private HttpCent cent;
    private LinearLayout wait;
    private String sessionid;
    private ImageView codeView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                codeView.setImageDrawable((Drawable) msg.obj);
            } else if (msg.what == 111) {
                Config.setSettings(getApplicationContext(), "jwuser", username.getText().toString().trim());
                Config.setSettings(getApplicationContext(), "jwpw", password.getText().toString().trim());
                Config.setSettings(getApplicationContext(), "sessionid", sessionid);
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                int hello = getIntent().getIntExtra("hello", 1);
                Intent i = null;
                if (hello == 1)
                    i = new Intent(LoginActivity.this, GradeActivity.class);
                if (hello == 2)
                    i = new Intent(LoginActivity.this, ScheduleActivity.class);
                if (hello == 3)
                    i = new Intent(LoginActivity.this, ExamActivity.class);
                Config.setSettings(getApplicationContext(), "jwstatus", "login");
                startActivity(i);
                finish();
            } else if (msg.what == 100) {
                Toast.makeText(getApplicationContext(), "用户名，密码或者验证码错误~", Toast.LENGTH_SHORT).show();
                wait.setVisibility(View.INVISIBLE);
                initData();
            } else if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
                wait.setVisibility(View.INVISIBLE);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        initData();
    }

    public void initUI() {
        code = (EditText) findViewById(R.id.codeTxt);
        codeView = (ImageView) findViewById(R.id.code);
        codeView.setOnClickListener(this);
        wait = (LinearLayout) findViewById(R.id.waitbar_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        username.setText(Config.getSettings(this, "jwuser"));
        password.setText(Config.getSettings(this, "jwpw"));
    }

    public void refresh() {
        try {
            Drawable drawable = cent.getValidateCode(sessionid);
            Message msg = new Message();
            msg.what = 1;
            msg.obj = drawable;
            handler.sendMessage(msg);
        } catch (Exception e) {
            handler.sendEmptyMessage(0);
        }
    }

    public void initData() {
        new Thread() {
            public void run() {
                cent = new HttpCent();
                try {
                    sessionid = cent.initCent();
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                }

                try {
                    Drawable drawable = cent.getValidateCode(sessionid);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = drawable;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                }

            }

            ;
        }.start();
    }

    public void login(View view) {
        wait.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                String cde = code.getText().toString().trim();
                String usr = username.getText().toString().trim();
                String pw = password.getText().toString().trim();
                try {
                    boolean bool = cent.login(sessionid, usr, pw, cde);
                    if (bool) {
                        handler.sendEmptyMessage(111);
                    } else {
                        handler.sendEmptyMessage(100);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(100);
                }
            }

            ;
        }.start();
    }

    @Override
    public void onClick(View v) {
        refresh();
    }
}

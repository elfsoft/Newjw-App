package cn.elfsoft.assist.loginactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.activity.MainActivity;
import cn.elfsoft.assist.logic.LoginCent;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class InitLoginActivity extends Activity implements OnClickListener, Callback {
    private static String APPKEY = "27e6155230fc";
    private static String APPSECRET = "353d12a1b810543edec1efef218e9c9f";

    private boolean ready;
    private LoginCent cent;
    private EditText phoneNum, pw;
    private LinearLayout wait;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                wait.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                Config.setSettings(getApplicationContext(), "elfphone", phoneNum.getText().toString().trim());
                Config.setSettings(getApplicationContext(), "elfpw", pw.getText().toString().trim());
                Config.setSettings(getApplicationContext(), "elfname", (String) msg.obj);
                Config.setSettings(getApplicationContext(), "loginstatus", "1");
                finish();
                Intent intent = new Intent(InitLoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (msg.what == 10) {
                Toast.makeText(getApplicationContext(), "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                wait.setVisibility(View.INVISIBLE);
            } else if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
                wait.setVisibility(View.INVISIBLE);
            } else if (msg.what == 32) {
                Toast.makeText(getApplicationContext(), "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
                wait.setVisibility(View.INVISIBLE);
            }
        }

        ;
    };

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cent = new LoginCent();
        initSDK();
        setContentView(R.layout.activity_init_login);
        initUI();
    }

    ;

    private void initUI() {
        wait = (LinearLayout) findViewById(R.id.waitbar);
        phoneNum = (EditText) findViewById(R.id.phone);
        pw = (EditText) findViewById(R.id.pw);
        phoneNum.setText(Config.getSettings(this, "elfphone"));
        pw.setText(Config.getSettings(this, "elfpw"));
    }

    public void regist(View v) {
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String phone = (String) phoneMap.get("phone");
                    System.out.println(phone);
                    Intent inent = new Intent(InitLoginActivity.this, SmsRegistActivity.class);
                    inent.putExtra("phone", phone);
                    startActivity(inent);
                    finish();
                }
            }
        });
        registerPage.show(this);

    }

    public void login(View v) {
        wait.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                String phone = phoneNum.getText().toString().trim();
                String psd = pw.getText().toString().trim();
                if (phone.equals("") || psd.equals("")) {
                    handler.sendEmptyMessage(32);
                    return;
                }
                try {
                    String[] str = cent.login(phone, psd).split("-");
                    if (str[0].equals("true")) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = str[1];
                        if (str[2].equals("null") || str[3].equals("null") || str[4].equals("null")) {

                        } else {
                            Config.setSettings(getApplicationContext(), "campususer", str[2]);
                            Config.setSettings(getApplicationContext(), "jwuser", str[2]);
                            Config.setSettings(getApplicationContext(), "campuspw", str[4]);
                            Config.setSettings(getApplicationContext(), "jwpw", str[3]);
                        }
                        handler.sendMessage(msg);
                    } else {
                        handler.sendEmptyMessage(10);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    private void initSDK() {
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        final Handler handler = new Handler(this);
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
        ready = true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    protected void onDestroy() {
        if (ready) {
            SMSSDK.unregisterAllEventHandler();
        }
        super.onDestroy();
    }
}

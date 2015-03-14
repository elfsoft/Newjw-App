package cn.elfsoft.assist.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.modle.JsonModel;
import cn.elfsoft.assist.logic.InSchoolHttpCent;

public class InSchoolLoginActivity extends Activity {
    private EditText username, password;
    private LinearLayout wait;
    private String sessionid;
    private TextView title;
    private InSchoolHttpCent cent;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(InSchoolLoginActivity.this, LeaveInfoActivity.class);
                    startActivity(intent);
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
                    break;
                case 10:

                    break;
                case 11:
                    Config.setSettings(getApplicationContext(), "campususer", username.getText().toString().trim());
                    Config.setSettings(getApplicationContext(), "campuspw", password.getText().toString().trim());
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    Config.setSettings(getApplicationContext(), "jsessionid", sessionid);
                    String[] str = ((String) (msg.obj)).split("-");
                    int hello = getIntent().getIntExtra("hello", 2);
                    if (hello == 2) {
                        Intent i = new Intent(InSchoolLoginActivity.this, KaoQinActivity.class);
                        startActivity(i);
                    } else if (hello == 1) {
                        Intent i = new Intent(InSchoolLoginActivity.this, LeaveInfoActivity.class);
                        i.putExtra("teachername", str[0]);
                        i.putExtra("teacherno", str[1]);
                        i.putExtra("status", 1);
                        startActivity(i);
                    }
                    finish();
                    break;
                case 110:
                    Toast.makeText(getApplicationContext(), "用户名或者密码错误~", Toast.LENGTH_SHORT).show();
                    break;
            }
            wait.setVisibility(View.INVISIBLE);
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cent = new InSchoolHttpCent();
        sessionid = Config.getSettings(this, "jsessionid");
        setContentView(R.layout.activity_login);
        initUI();

    }

    public void initUI() {
        title = (TextView) findViewById(R.id.login_title_name);
        title.setText("校园网登录");
        wait = (LinearLayout) findViewById(R.id.waitbar_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        username.setText(Config.getSettings(this, "campususer"));
        password.setText(Config.getSettings(this, "campuspw"));

    }

    public void login(View v) {
        wait.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                String user = username.getText().toString().trim();
                String pw = password.getText().toString().trim();
                try {
                    sessionid = cent.getSessionId();
                    boolean bool = cent.login(user, pw);
                    if (bool) {
                        JsonModel model = cent.getTeacherInfo(user);
                        String str = model.getTeacherList().get(0).getItemName() + "-" + model.getTeacherList().get(0).getItemNo();
                        Message msg = new Message();
                        msg.what = 11;
                        msg.obj = str;
                        handler.sendMessage(msg);
                    } else
                        handler.sendEmptyMessage(110);
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }
}

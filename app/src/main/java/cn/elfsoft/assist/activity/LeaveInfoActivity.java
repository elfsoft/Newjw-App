package cn.elfsoft.assist.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.loginactivity.InitLoginActivity;
import cn.elfsoft.assist.modle.JsonModel;
import cn.elfsoft.assist.modle.Rows;
import cn.elfsoft.assist.util.AtyList;
import cn.elfsoft.assist.logic.InSchoolHttpCent;
import cn.elfsoft.assist.logic.ShareLogic;

public class LeaveInfoActivity extends Activity implements OnItemClickListener {
    private int pos;
    private TextView uname, profession;
    private InSchoolHttpCent cent;
    private List<Map<String, String>> list;
    private ListView leaveView;
    private SimpleAdapter adapter;
    private LinearLayout wait;
    private int status = 0;
    private String teachername, teacherno;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 11:
                    if (msg.obj != null) {
                        String[] temp = ((String) (msg.obj)).split("-");
                        uname.setText(temp[0]);
                        profession.setText(temp[1]);
                    }
                    adapter.notifyDataSetChanged();
                    wait.setVisibility(View.INVISIBLE);
                    break;
                case 123:
                    Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    onRestart();
                    break;
                case 321:
                    Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    wait.setVisibility(View.INVISIBLE);
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
                    wait.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_info);
        status = getIntent().getIntExtra("status", 0);
        cent = new InSchoolHttpCent();
        list = new ArrayList<Map<String, String>>();
        Intent intent = getIntent();
        teachername = intent.getStringExtra("teachername");
        teacherno = intent.getStringExtra("teacherno");
        initUI();
        initData();
        initAdapter();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        list.clear();
        wait.setVisibility(View.VISIBLE);
        status = 1;
        initData();
    }

    public void initUI() {
        uname = (TextView) findViewById(R.id.username);
        profession = (TextView) findViewById(R.id.profession);
        wait = (LinearLayout) findViewById(R.id.waitbar_leave);
        leaveView = (ListView) findViewById(R.id.leave_list);
        leaveView.setOnItemClickListener(this);
    }

    public void initAdapter() {
        String[] param = {"leaveYear", "leaveType", "leaveTeacher", "leaveStatus", "leaveStart", "leaveEnd", "leaveReason"};
        int[] res = {R.id.leaveYear, R.id.leaveType, R.id.leaveTeacher, R.id.leaveStatus, R.id.leaveStart, R.id.leaveEnd, R.id.leaveReason};
        adapter = new SimpleAdapter(this, list, R.layout.list_leave_info, param, res);
        leaveView.setAdapter(adapter);
    }

    public void initData() {
        new Thread() {
            public void run() {
                try {

                    boolean bool = false;
                    if (status == 0) {
                        String sessionid = cent.getSessionId();
                        bool = cent.login(Config.getSettings(getApplicationContext(), "campususer"),
                                Config.getSettings(getApplicationContext(), "campuspw"));
                        Config.setSettings(getApplicationContext(), "jsessionid", sessionid);
                    } else {
                        cent.setSessionid(Config.getSettings(getApplicationContext(), "jsessionid"));
                        bool = true;
                    }
                    if (bool) {
                        JsonModel model = cent.getTeacherInfo(Config.getSettings(getApplicationContext(), "campususer"));
                        teachername = model.getTeacherList().get(0).getItemName();
                        teacherno = model.getTeacherList().get(0).getItemNo();
                        model = cent.getLeaveInfo();
                        List<Rows> rows = model.getRows();
                        for (int i = 0; i < rows.size(); i++) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("aflid", rows.get(i).getAflid());
                            map.put("leaveYear", rows.get(i).getAcademicyearno());
                            String type = rows.get(i).getAfltype();
                            if (type.equals("1"))
                                map.put("leaveType", "病假");
                            else
                                map.put("leaveType", "事假");
                            map.put("leaveTeacher", rows.get(i).getTeachername());
                            String check = rows.get(i).getIscheck();
                            if (check.equals("0")) {
                                map.put("leaveStatus", "待审核");
                            } else if (check.equals("1")) {
                                map.put("leaveStatus", "ͨ通过");
                            } else {
                                map.put("leaveStatus", "未通过");
                            }
                            map.put("leaveStart", rows.get(i).getStartdate() + "/" + rows.get(i).getStartsection());
                            map.put("leaveEnd", rows.get(i).getEnddate() + "/" + rows.get(i).getEndsection());
                            map.put("leaveReason", rows.get(i).getReason());
                            list.add(map);
                        }
                        Message msg = new Message();
                        msg.what = 11;
                        if (rows.size() != 0) {
                            msg.obj = rows.get(0).getClassname() + "-" + rows.get(0).getStudentname();
                            System.out.println(msg.obj);
                        }
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                }
            }

            ;
        }.start();
    }

    public void leave(View v) {
        Intent intent = new Intent(this, LeaveApplyActivity.class);
        intent.putExtra("teachername", teachername);
        intent.putExtra("teacherno", teacherno);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.pos = position;
        new AlertDialog.Builder(this).setTitle("是否要删除请假信息？").setMessage("请谨慎删除已通过审核的信息").setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wait.setVisibility(View.VISIBLE);
                new Thread() {
                    public void run() {
                        try {
                            boolean bool = cent.deleteLeaveInfo(list.get(pos).get("aflid"));
                            if (bool)
                                handler.sendEmptyMessage(123);
                            else
                                handler.sendEmptyMessage(321);
                        } catch (UnsupportedEncodingException e) {
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            handler.sendEmptyMessage(0);
                        }
                    }

                    ;
                }.start();
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu3, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cmplogout) {
            Intent i = new Intent(this, InSchoolLoginActivity.class);
            startActivity(i);
            finish();
        }
        if (item.getItemId() == R.id.logout) {
            Intent it = new Intent(this, InitLoginActivity.class);
            startActivity(it);
            finish();
            Config.setSettings(this, "elfphone", "");
            AtyList.list.get(0).finish();
        }
        if (item.getItemId() == R.id.share) {
            ShareLogic.showShare(this, 5, this);
        }
        return super.onOptionsItemSelected(item);
    }
}

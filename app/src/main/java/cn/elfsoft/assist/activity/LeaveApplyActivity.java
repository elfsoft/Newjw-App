package cn.elfsoft.assist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.logic.InSchoolHttpCent;

public class LeaveApplyActivity extends Activity implements OnItemSelectedListener {
    private DatePicker startdate, enddate;
    private String startdatestr, enddatestr;
    private Spinner startsection, endsection;
    private String teachername, teacherno;
    private String startsectionstr, endsectionstr;
    private TextView teacher;
    private EditText phone, reason;
    private InSchoolHttpCent cent;
    private LinearLayout wait;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what != 0) {
                wait.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "请假成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                wait.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_apply);
        cent = new InSchoolHttpCent();
        cent.setSessionid(Config.getSettings(this, "jsessionid"));
        Intent intent = getIntent();
        teachername = intent.getStringExtra("teachername");
        teacherno = intent.getStringExtra("teacherno");
        initUI();
        initSections();
        initPickers();
    }

    public void initUI() {
        wait = (LinearLayout) findViewById(R.id.waitbar_leave);
        phone = (EditText) findViewById(R.id.telphone);
        reason = (EditText) findViewById(R.id.reason);
        teacher = (TextView) findViewById(R.id.teacher);
        teacher.setText(teachername);
        startdate = (DatePicker) findViewById(R.id.startdate);
        enddate = (DatePicker) findViewById(R.id.enddate);
        startsection = (Spinner) findViewById(R.id.startsection);
        endsection = (Spinner) findViewById(R.id.endsection);
        startsection.setOnItemSelectedListener(this);
        endsection.setOnItemSelectedListener(this);
    }

    public void initSections() {
        String[] items = getResources().getStringArray(R.array.section);
        startsection.setAdapter(new ArrayAdapter<String>(this, R.layout.myspinner, android.R.id.text1, items));
        endsection.setAdapter(new ArrayAdapter<String>(this, R.layout.myspinner, android.R.id.text1, items));
    }

    public void initPickers() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        startdatestr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        enddatestr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        startdate.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startdatestr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            }
        });
        enddate.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                enddatestr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            }
        });
    }

    public void leave(View v) {
        wait.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                String tel = phone.getText().toString().trim();
                String reasons = reason.getText().toString();
                try {
                    cent.addLeaveInfo(startdatestr, enddatestr, startsectionstr, endsectionstr, teachername, teacherno, tel, reasons);
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String temp = position + "";
        if (position < 10) {
            temp = "0" + position;
        }
        if (parent.getId() == R.id.startsection) {
            startsectionstr = temp;
        } else {
            endsectionstr = temp;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

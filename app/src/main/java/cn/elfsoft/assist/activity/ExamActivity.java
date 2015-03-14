package cn.elfsoft.assist.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.loginactivity.InitLoginActivity;
import cn.elfsoft.assist.modle.Bar;
import cn.elfsoft.assist.util.AtyList;
import cn.elfsoft.assist.logic.HttpCent;
import cn.elfsoft.assist.logic.ShareLogic;

public class ExamActivity extends Activity implements OnItemSelectedListener {
    private Spinner bar, termBar;
    private ListView gradeView;
    private List<String> list;
    private String[] terms;
    private HttpCent cent;
    private LinearLayout waitbar;
    private List<Bar> barList;
    ArrayAdapter<String> barAdapter;
    private String term, type;
    private List<Map<String, String>> mList;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 12:
                    barAdapter.notifyDataSetChanged();
                    bar.setOnItemSelectedListener(ExamActivity.this);
                    waitbar.setVisibility(View.INVISIBLE);
                    break;
                case 13:
                    initGradeView();
                    waitbar.setVisibility(View.INVISIBLE);
                    gradeView.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
                    waitbar.setVisibility(View.INVISIBLE);
                    break;
                case 110:
                    Toast.makeText(getApplicationContext(), "Session已失效，请重新登录~", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(ExamActivity.this, LoginActivity.class);
                    it.putExtra("hello", 3);
                    startActivity(it);
                    finish();
                    break;
            }

        }

        ;
    };

    public void initGradeView() {
        String[] arr = {"class", "rank", "type", "time", "location", "position"};
        int[] irr = {R.id.exam_classs, R.id.exam_rank, R.id.exam_type, R.id.exam_time, R.id.exam_location, R.id.exam_position};
        SimpleAdapter adapter = new SimpleAdapter(this, mList, R.layout.exam_list_item, arr, irr);
        gradeView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        cent = new HttpCent();
        initUI();
        initData();
    }

    public void initUI() {
        waitbar = (LinearLayout) findViewById(R.id.waitbar);
        termBar = (Spinner) findViewById(R.id.grade_term_bar);
        bar = (Spinner) findViewById(R.id.grade_bar);
        gradeView = (ListView) findViewById(R.id.grade_list);
    }

    public void initData() {
        list = new ArrayList<String>();
        barAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, list);
        bar.setAdapter(barAdapter);

        terms = getResources().getStringArray(R.array.exam_spinner);
        ArrayAdapter<String> termBarAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, terms);
        termBar.setAdapter(termBarAdapter);
        termBar.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = terms[position];
                if (str.equals("中考")) {
                    type = "3";
                } else if (str.equals("末考")) {
                    type = "2";
                } else if (str.equals("补考")) {
                    type = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        new Thread() {
            public void run() {
                try {
                    barList = cent.getExamBarInfo(Config.getSettings(getApplicationContext(), "sessionid"));
                    if (barList == null) {
                        Intent intent = new Intent(ExamActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return;
//						String sessionid = cent.initCent();
//						String username = Config.getSettings(getApplicationContext(), "jwuser");
//						String password = Config.getSettings(getApplicationContext(), "jwpw");
//						cent.login(sessionid, username, password);
//						Config.setSettings(getApplicationContext(), "sessionid", sessionid);
//						barList = cent.getExamBarInfo(Config.getSettings(getApplicationContext(), "sessionid"));
                    }
                    for (int i = 0; i < barList.size(); i++) {
                        list.add(barList.get(i).getName());
                        System.out.println(barList.get(i).getName());
                    }
                    handler.sendEmptyMessage(12);
                } catch (XmlPullParserException xmle) {
                    handler.sendEmptyMessage(110);
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                }
            }

            ;
        }.start();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        term = barList.get(position).getTerm();
        System.out.println(term);
    }

    public void search(View v) {
        waitbar.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                try {
                    mList = cent.getExamInfo(Config.getSettings(getApplicationContext(), "sessionid"), term, type);
                    handler.sendEmptyMessage(13);
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                }
            }

            ;
        }.start();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.jwlogout) {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("hello", 3);
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
            ShareLogic.showShare(this, 4, this);
        }
        return super.onOptionsItemSelected(item);
    }
}

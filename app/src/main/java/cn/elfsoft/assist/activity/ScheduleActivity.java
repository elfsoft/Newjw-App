package cn.elfsoft.assist.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
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

public class ScheduleActivity extends Activity implements OnItemSelectedListener {
    private Spinner scheduleSpinner;
    private LinearLayout wait;
    private ScrollView content;
    private TextView oneOne, oneTwo, oneThree, oneFour, oneFive, oneSix, oneSeven;
    private TextView twoOne, twoTwo, twoThree, twoFour, twoFive, twoSix, twoSeven;
    private TextView threeOne, threeTwo, threeThree, threeFour, threeFive, threeSix, threeSeven;
    private TextView fourOne, fourTwo, fourThree, fourFour, fourFive, fourSix, fourSeven;
    private TextView fiveOne, fiveTwo, fiveThree, fiveFour, fiveFive, fiveSix, fiveSeven;
    private HttpCent cent;
    private ArrayAdapter<String> adapter;
    private List<String> bar;
    private List<Bar> list;
    private String term;
    private String hidyzm = "";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:
                    adapter.notifyDataSetChanged();
                    scheduleSpinner.setOnItemSelectedListener(ScheduleActivity.this);
                    wait.setVisibility(View.INVISIBLE);
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
                    wait.setVisibility(View.INVISIBLE);
                    break;
                case 123:
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, String> map = list.get(i);
                        int x = Integer.parseInt(map.get("item"));
                        String temp = map.get("info");
                        System.out.println("temp" + temp);
                        if (temp != null && !temp.equals("")) {
                            String[] infos = map.get("info").split("#");
                            String info = "";
                            for (int j = 0; j < infos.length; j++) {
                                String[] result = infos[j].split("@");
                                info += result[0] + "\n" + result[1] + "\n" + result[2] + "\n" + result[3] + "\n";
                            }
                            System.out.println(info);
                            buildSchedule(x, info);
                        }
                    }
                    wait.setVisibility(View.INVISIBLE);
                    content.setVisibility(View.VISIBLE);
                    break;
                case 110:
                    Toast.makeText(getApplicationContext(), "Session已失效，请重新登录~", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(ScheduleActivity.this, LoginActivity.class);
                    it.putExtra("hello", 2);
                    startActivity(it);
                    finish();
                    break;
            }
        }

        ;
    };

    public void buildSchedule(int i, String info) {
        switch (i) {
            case 11:
                oneOne.setText(info);
                break;
            case 12:
                oneTwo.setText(info);
                break;
            case 13:
                oneThree.setText(info);
                break;
            case 14:
                oneFour.setText(info);
                break;
            case 15:
                oneFive.setText(info);
                break;
            case 16:
                oneSix.setText(info);
                break;
            case 17:
                oneSeven.setText(info);
                break;
            case 21:
                twoOne.setText(info);
                break;
            case 22:
                twoTwo.setText(info);
                break;
            case 23:
                twoThree.setText(info);
                break;
            case 24:
                twoFour.setText(info);
                break;
            case 25:
                twoFive.setText(info);
                break;
            case 26:
                twoSix.setText(info);
                break;
            case 27:
                twoSeven.setText(info);
                break;
            case 31:
                threeOne.setText(info);
                break;
            case 32:
                threeTwo.setText(info);
                break;
            case 33:
                threeThree.setText(info);
                break;
            case 34:
                threeFour.setText(info);
                break;
            case 35:
                threeFive.setText(info);
                break;
            case 36:
                threeSix.setText(info);
                break;
            case 37:
                threeSeven.setText(info);
                break;
            case 41:
                fourOne.setText(info);
                break;
            case 42:
                fourTwo.setText(info);
                break;
            case 43:
                fourThree.setText(info);
                break;
            case 44:
                fourFour.setText(info);
                break;
            case 45:
                fourFive.setText(info);
                break;
            case 46:
                fourSix.setText(info);
                break;
            case 47:
                fourSeven.setText(info);
                break;
            case 51:
                fiveOne.setText(info);
                break;
            case 52:
                fiveTwo.setText(info);
                break;
            case 53:
                fiveThree.setText(info);
                break;
            case 54:
                fiveFour.setText(info);
                break;
            case 55:
                fiveFive.setText(info);
                break;
            case 56:
                fiveSix.setText(info);
                break;
            case 57:
                fiveSeven.setText(info);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        cent = new HttpCent();
        initUI();
        initData();
    }

    public void initUI() {
        content = (ScrollView) findViewById(R.id.scroll_content);
        wait = (LinearLayout) findViewById(R.id.wait_schedule);
        scheduleSpinner = (Spinner) findViewById(R.id.schedule_bar);
        oneOne = (TextView) findViewById(R.id.one_one);
        oneTwo = (TextView) findViewById(R.id.one_two);
        oneThree = (TextView) findViewById(R.id.one_three);
        oneFour = (TextView) findViewById(R.id.one_four);
        oneFive = (TextView) findViewById(R.id.one_five);
        oneSix = (TextView) findViewById(R.id.one_six);
        oneSeven = (TextView) findViewById(R.id.one_seven);
        twoOne = (TextView) findViewById(R.id.two_one);
        twoTwo = (TextView) findViewById(R.id.two_two);
        twoThree = (TextView) findViewById(R.id.two_three);
        twoFour = (TextView) findViewById(R.id.two_four);
        twoFive = (TextView) findViewById(R.id.two_five);
        twoSix = (TextView) findViewById(R.id.two_six);
        twoSeven = (TextView) findViewById(R.id.two_seven);
        threeOne = (TextView) findViewById(R.id.three_one);
        threeTwo = (TextView) findViewById(R.id.three_two);
        threeThree = (TextView) findViewById(R.id.three_three);
        threeFour = (TextView) findViewById(R.id.three_four);
        threeFive = (TextView) findViewById(R.id.three_five);
        threeSix = (TextView) findViewById(R.id.three_six);
        threeSeven = (TextView) findViewById(R.id.three_seven);
        fourOne = (TextView) findViewById(R.id.four_one);
        fourTwo = (TextView) findViewById(R.id.four_two);
        fourThree = (TextView) findViewById(R.id.four_three);
        fourFour = (TextView) findViewById(R.id.four_four);
        fourFive = (TextView) findViewById(R.id.four_five);
        fourSix = (TextView) findViewById(R.id.four_six);
        fourSeven = (TextView) findViewById(R.id.four_seven);
        fiveOne = (TextView) findViewById(R.id.five_one);
        fiveTwo = (TextView) findViewById(R.id.five_two);
        fiveThree = (TextView) findViewById(R.id.five_three);
        fiveFour = (TextView) findViewById(R.id.five_four);
        fiveFive = (TextView) findViewById(R.id.five_five);
        fiveSix = (TextView) findViewById(R.id.five_six);
        fiveSeven = (TextView) findViewById(R.id.five_seven);
    }

    public void initData() {
        bar = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, bar);
        scheduleSpinner.setAdapter(adapter);
        new Thread() {
            public void run() {
                try {
                    list = cent.getScheduleBarInfo(Config.getSettings(getApplicationContext(), "sessionid"));
                    if (list == null) {
                        Intent intent = new Intent(ScheduleActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return;
//						String sessionid = cent.initCent();
//						String username = Config.getSettings(getApplicationContext(), "jwuser");
//						String password = Config.getSettings(getApplicationContext(), "jwpw");
//						cent.login(sessionid, username, password);
//						Config.setSettings(getApplicationContext(), "sessionid", sessionid);
//						list = cent.getScheduleBarInfo(Config.getSettings(getApplicationContext(), "sessionid"));
                    }
                    if (list.size() > 0)
                        hidyzm = list.get(0).getHidyzm();
                    for (int i = 0; i < list.size(); i++) {
                        bar.add(list.get(i).getName());
                        handler.sendEmptyMessage(111);
                    }

                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                }
            }

            ;
        }.start();
    }

    public void search(View v) {
        wait.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                try {
                    Message msg = new Message();
                    msg.what = 123;
                    msg.obj = cent.getScheduleInfo(Config.getSettings(getApplicationContext(), "sessionid"), term,hidyzm);
                    handler.sendMessage(msg);
                } catch (XmlPullParserException xmle) {
                    handler.sendEmptyMessage(110);
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
        term = list.get(position).getTerm();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            i.putExtra("hello", 2);
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
            ShareLogic.showShare(this, 3, this);
        }
        return super.onOptionsItemSelected(item);
    }
}

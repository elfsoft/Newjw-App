package cn.elfsoft.assist.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.modle.AttendsModel;
import cn.elfsoft.assist.modle.AttendsRows;
import cn.elfsoft.assist.logic.InSchoolHttpCent;

public class KaoQinActivity extends Activity {
	private LinearLayout wait;
	private ListView kaoQin;
	private InSchoolHttpCent cent;
	private int status = 0;
	private SimpleAdapter adapter;
	private List<Map<String, String>> data;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				Toast.makeText(getApplicationContext(), "网络连接错误，请重试~", Toast.LENGTH_SHORT).show();
				wait.setVisibility(View.INVISIBLE);
			} else {
				adapter.notifyDataSetChanged();
				wait.setVisibility(View.INVISIBLE);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kao_qin);
		cent = new InSchoolHttpCent();
		status = getIntent().getIntExtra("status", 0);
		initUI();
		initAdapter();
		initData();

	}

	public void initUI() {
		kaoQin = (ListView) findViewById(R.id.kaoqin);
		wait = (LinearLayout) findViewById(R.id.wait);
	}

	public void initAdapter() {
		data = new ArrayList<Map<String, String>>();
		String[] from = { "course", "time", "kaoqin_status", "check_status" };
		int[] to = { R.id.course, R.id.time, R.id.kaoqin_status, R.id.check_status };
		adapter = new SimpleAdapter(this, data, R.layout.kao_qin_list_item, from, to);
		kaoQin.setAdapter(adapter);
	}

	public void initData() {

		new Thread() {
			@Override
			public void run() {
				boolean bool = false;
				try {
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
						AttendsModel model = cent.getKaoQinInfo();

						List<AttendsRows> rows = model.getStuAttends().getRows();
						for (int i = 0; i < rows.size(); i++) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("course", rows.get(i).getCourseName());
							map.put("time", rows.get(i).getAttDate());
							map.put("kaoqin_status", rows.get(i).getAttTypeName());
							int temp = rows.get(i).getIsVerified();
							if (temp == 0) {
								map.put("check_status", "审核中");
							} else {
								map.put("check_status", "已经审核");
							}
							data.add(map);
						}
						handler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					handler.sendEmptyMessage(0);
				}
			}
		}.start();
	}
}

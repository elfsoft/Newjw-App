package cn.elfsoft.assist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.elfsoft.assist.util.Config;
import cn.elfsoft.assist.R;
import cn.elfsoft.assist.loginactivity.InitLoginActivity;
import cn.elfsoft.assist.util.AtyList;
import cn.elfsoft.assist.logic.ShareLogic;

public class MainActivity extends Activity implements OnItemClickListener {
    private GridView grid, grid2;
    private TextView titleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AtyList.list.add(this);
        setContentView(R.layout.activity_main);
        initUI();
        initData();
//		ActionBar actionBar = getActionBar();
//		actionBar.show();
    }

    public void initUI() {
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("亲爱的 " + Config.getSettings(this, "elfname") + " ，欢迎使用掌上东软 ^_^~");
        grid2 = (GridView) findViewById(R.id.gridview2);
        grid = (GridView) findViewById(R.id.gridview);
        grid.setOnItemClickListener(this);
        grid2.setOnItemClickListener(this);
    }

    public void initData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "精灵公告");
        map.put("image", R.drawable.news);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "成绩查询");
        map.put("image", R.drawable.search);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "课表查询");
        map.put("image", R.drawable.schedule);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "考试查询");
        map.put("image", R.drawable.exam);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "空教室查询");
        map.put("image", R.drawable.emtyroom);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "社区");
        map.put("image", R.drawable.social);
        list.add(map);
        int[] ui = {R.id.name, R.id.image};
        String[] name = {"name", "image"};
        grid.setAdapter(new SimpleAdapter(this, list, R.layout.grid_item_style, name, ui));
        list = null;
        list = new ArrayList<Map<String, Object>>();
        map = new HashMap<String, Object>();
        map.put("name", "考勤信息");
        map.put("image", R.drawable.kaoqin);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "请假");
        map.put("image", R.drawable.holiday);
        list.add(map);
        grid2.setAdapter(new SimpleAdapter(this, list, R.layout.grid_item_style, name, ui));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Config.setSettings(this, "loginstatus", "0");
            Intent it = new Intent(this, InitLoginActivity.class);
            startActivity(it);
            finish();
        } else if (item.getItemId() == R.id.share) {
            ShareLogic.showShare(this, 1, this);
        } else if (item.getItemId() == R.id.about) {
            Intent it = new Intent(this, AboutActivity.class);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String status = Config.getSettings(this, "jwstatus");
        if (parent.getId() == R.id.gridview)
            switch (position) {
                case 0:
                    Intent inten = new Intent(MainActivity.this, ElfActivity.class);
                    startActivity(inten);
                    break;
                case 1:
                    if (status.equals("login")) {
                        Intent intent = new Intent(MainActivity.this, GradeActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("hello", 1);
                        startActivity(intent);
                    }
                    break;
                case 2:
                    if (status.equals("login")) {
                        Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("hello", 2);
                        startActivity(intent);
                    }
                    break;
                case 3:
                    if (status.equals("login")) {
                        Intent intent = new Intent(MainActivity.this, ExamActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("hello", 3);
                        startActivity(intent);
                    }
                    break;
                case 4:
                    Intent intent = new Intent(MainActivity.this, EmptyRoomActivity.class);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "功能暂未开放，敬请期待", Toast.LENGTH_SHORT).show();
            }
        else if (parent.getId() == R.id.gridview2)
            switch (position) {
                case 1:
                    if (!Config.getSettings(getApplicationContext(), "campususer").equals("")) {
                        Intent intent = new Intent(MainActivity.this, LeaveInfoActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, InSchoolLoginActivity.class);
                        intent.putExtra("hello", 1);
                        startActivity(intent);
                    }
                    break;
                case 0:
                    if (!Config.getSettings(getApplicationContext(), "campususer").equals("")) {
                        Intent intent = new Intent(MainActivity.this, KaoQinActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, InSchoolLoginActivity.class);
                        intent.putExtra("hello", 2);
                        startActivity(intent);
                    }
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "功能暂未开放，敬请期待", Toast.LENGTH_SHORT).show();
            }

    }
}

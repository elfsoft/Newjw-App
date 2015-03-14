package cn.elfsoft.assist.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.elfsoft.assist.R;

public class EmptyRoomActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_room);
        initUI();
        initData();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initUI() {
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void initData() {
        webView.loadUrl("http://newjw2.neusoft.edu.cn/jwweb/ZNPK/KBFB_RoomSel.aspx");
    }
}

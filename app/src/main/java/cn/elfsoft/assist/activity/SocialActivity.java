package cn.elfsoft.assist.activity;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import cn.elfsoft.assist.R;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.socialization.Comment;
import cn.sharesdk.socialization.Socialization;

public class SocialActivity extends Activity {
    private Socialization social;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        ShareSDK.initSDK(this);
        ShareSDK.registerService(Socialization.class);
        social = ShareSDK.getService(Socialization.class);
        new Thread() {
            public void run() {
                social.replyTopic(10001, "assist0000000001", "��������", "��ð� Helloworld");
                @SuppressWarnings("unchecked")
                ArrayList<Comment> comments = social.getCommentList("assist0000000001");
                System.out.println(comments);
            }

            ;
        }.start();

    }
}

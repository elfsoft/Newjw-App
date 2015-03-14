package cn.elfsoft.assist.logic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;

import cn.elfsoft.assist.R;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareLogic {

    public static void showShare(Context context, int type, Activity aty) {
        String content = "";
        String img = "";
        int i = 0;
        if (type == 1) {
            i = 1;
            img = "http://www.elfsoft.cn/content/uploadfile/201409/99e71409921572.png";
            content = "我正在使用掌上东软，真的是太棒了；可以查课表，查成绩，查考试时间地点，还可以在线请假呢~，东软的小伙伴儿们，还在等什么呢，快来下载吧~！ ^_^";
        } else if (type == 5) {
            content = "我刚刚使用掌上东软向导员请假了，电脑不在身边的小伙伴儿们，再也不用让别人帮忙请假了，只要有Neusoft随时随地可以请假~！";
            GetandSaveCurrentImage(context, aty);
            img = "/sdcard/elfsoft/ScreenImages/screen.png";
        } else if (type == 2) {
            content = "我刚刚用掌上东软在手机上查了上学期的成绩，快来下载，也查查自己的成绩吧~";
            GetandSaveCurrentImage(context, aty);
            img = "/sdcard/elfsoft/ScreenImages/screen.png";
        } else if (type == 3) {
            content = "我刚刚用掌上东软在手机上查了这学期的课表，不知道课表的小伙伴们，快来下载也查查自己的课表吧~";
            GetandSaveCurrentImage(context, aty);
            img = "/sdcard/elfsoft/ScreenImages/screen.png";
        } else if (type == 4) {
            content = "我刚刚用掌上东软在手机上查了最近的考试时间和地点，有考试的童鞋们，知道自己的考试时间地点吗？快来下载查询吧~";
            GetandSaveCurrentImage(context, aty);
            img = "/sdcard/elfsoft/ScreenImages/screen.png";
        }
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, context.getString(R.string.app_name) + "-精灵软件开发");
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(context.getString(R.string.app_name) + "-精灵软件开发");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.elfsoft.cn/?post=5");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数

        if (i == 1) {
            oks.setImageUrl(img);
        } else {
            oks.setImagePath(img);
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.elfsoft.cn/neusoft_assist.html");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("掌上东软-精灵软件开发");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        // oks.setSiteUrl("http://www.elfsoft.cn");

        // 启动分享GUI
        oks.show(context);
    }

    private static void GetandSaveCurrentImage(Context context, Activity aty) {
        // 构建Bitmap
        WindowManager windowManager = aty.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        // 获取屏幕
        View decorview = aty.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();
        // 图片存储路径
        String SavePath = getSdcardPath() + "/elfsoft/ScreenImages";
        // 保存Bitmap
        try {
            File path = new File(SavePath);
            // 文件
            String filepath = SavePath + "/screen.png";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
//				Toast.makeText(context, "截屏文件已保存至SDCard/ScreenImages/目录下", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().toString() + File.separator;
    }
}

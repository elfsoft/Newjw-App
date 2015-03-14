package cn.elfsoft.assist.logic;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginCent {
    protected String httpUrl = "http://api.elfsoft.cn/newjw/servlet/";

    // protected String httpUrl = "http://192.168.1.100:8080/newjw/servlet/";

    protected InputStream request2(String path) throws Exception {
        URL url = new URL(httpUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(2 * 1000);
        conn.setReadTimeout(2 * 1000);
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        InputStream in = conn.getInputStream();
        if (in == null)
            throw new Exception("连接失败");
        return in;
    }

    protected InputStream request(String path) throws Exception {
        for (int i = 0; i < 5; i++) {
            try {
                return request2(path);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    public boolean regist(String phone, String name, String pw) throws UnsupportedEncodingException, Exception {
        // String n = new String(name.getBytes("utf-8"), "ISO-8859-1");
        name = URLEncoder.encode(name, "utf-8");
        String path = "elfsoft?commond=regist&phone=" + phone + "&name=" + name + "&pw=" + pw;

        String result = new String(readInputStream(request(path)), "utf-8");
        System.out.println(result);
        if (result.equals("true")) {
            return true;
        }
        return false;
    }

    public String login(String phone, String pw) throws UnsupportedEncodingException, Exception {
        String path = "elfsoft?commond=elflogin&phone=" + phone + "&pw=" + pw;
        String result = new String(readInputStream(request(path)), "utf-8");
        System.out.println(result);
        return result;
    }

    protected static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }
}
